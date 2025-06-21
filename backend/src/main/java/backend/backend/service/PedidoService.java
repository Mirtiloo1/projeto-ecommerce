package backend.backend.service;

import backend.backend.model.Carrinho;
import backend.backend.model.Pedido;
import backend.backend.model.ItemPedido;
import backend.backend.model.Produto;
import backend.backend.model.Usuario;
import backend.backend.repository.CarrinhoRepository;
import backend.backend.repository.PedidoRepository;
import backend.backend.repository.ProdutoRepository;
import backend.backend.repository.UsuarioRepository;
import backend.backend.dto.PedidoResponseDTO;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private CarrinhoRepository carrinhoRepository;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            throw new RuntimeException("Usuário não autenticado ou principal inválido.");
        }
        String emailUsuario = ((UserDetails) authentication.getPrincipal()).getUsername();
        return usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado no banco de dados."));
    }

    // --- Criar um Pedido a partir do Carrinho ---
    @Transactional
    public PedidoResponseDTO criarPedidoDoCarrinho(String enderecoEntrega, String formaPagamento) {
        Usuario usuario = getUsuarioLogado();
        Carrinho carrinho = carrinhoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado para o usuário logado."));

        if (carrinho.getItens().isEmpty()) {
            throw new RuntimeException("O carrinho está vazio. Adicione itens antes de finalizar o pedido.");
        }

        Pedido novoPedido = new Pedido();
        novoPedido.setUsuario(usuario);
        novoPedido.setDataHoraPedido(LocalDateTime.now());
        novoPedido.setStatus("PENDENTE");
        novoPedido.setEnderecoEntrega(enderecoEntrega);
        novoPedido.setFormaPagamento(formaPagamento);

        List<ItemPedido> itensPedido = carrinho.getItens().stream().map(itemCarrinho -> {
            Produto produto = itemCarrinho.getProduto();
            Integer quantidadeComprada = itemCarrinho.getQuantidade();

            if (produto.getQuantidadeEstoque() < quantidadeComprada) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            // Atualizar estoque
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidadeComprada);
            produtoRepository.save(produto);

            // Criar ItemPedido
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(quantidadeComprada);
            itemPedido.setPrecoUnitario(produto.getPreco());
            itemPedido.setPedido(novoPedido);
            return itemPedido;
        }).collect(Collectors.toList());

        novoPedido.setItens(itensPedido);
        novoPedido.calcularValorTotal();

        Pedido pedidoSalvo = pedidoRepository.save(novoPedido);

        // Limpar o carrinho após o pedido ser finalizado
        carrinho.getItens().clear();
        carrinhoRepository.save(carrinho);

        return new PedidoResponseDTO(pedidoSalvo);
    }

    // --- Buscar Pedidos do Usuário Logado ---
    public Page<PedidoResponseDTO> buscarMeusPedidos(Pageable pageable) {
        Usuario usuario = getUsuarioLogado();
        Page<Pedido> pedidos = pedidoRepository.findByUsuario(usuario, pageable);
        return pedidos.map(PedidoResponseDTO::new);
    }

    // --- Buscar Todos os Pedidos (Apenas para Admin) ---
    public Page<PedidoResponseDTO> buscarTodosPedidos(Pageable pageable) {
        Page<Pedido> pedidos = pedidoRepository.findAll(pageable);
        return pedidos.map(PedidoResponseDTO::new);
    }

    // --- Buscar Pedido por ID (Para Usuário ou Admin) ---
    public Optional<PedidoResponseDTO> buscarPedidoPorId(Long id) {
        return pedidoRepository.findById(id).map(PedidoResponseDTO::new);
    }

    // --- Atualizar Status do Pedido (Apenas para Admin) ---
    @Transactional
    public Optional<PedidoResponseDTO> atualizarStatusPedido(Long id, String novoStatus) {
        return pedidoRepository.findById(id).map(pedido -> {

            pedido.setStatus(novoStatus);
            Pedido pedidoAtualizado = pedidoRepository.save(pedido);
            return new PedidoResponseDTO(pedidoAtualizado);
        });
    }

    // --- Cancelar Pedido (Pode ser por Usuário ou Admin) ---
    @Transactional
    public Optional<PedidoResponseDTO> cancelarPedido(Long id) {
        return pedidoRepository.findById(id).map(pedido -> {
            if (!"PENDENTE".equals(pedido.getStatus())) {
                throw new RuntimeException("Apenas pedidos PENDENTES podem ser cancelados.");
            }
            pedido.setStatus("CANCELADO");

            pedido.getItens().forEach(item -> {
                Produto produto = item.getProduto();
                produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + item.getQuantidade());
                produtoRepository.save(produto);
            });

            Pedido pedidoAtualizado = pedidoRepository.save(pedido);
            return new PedidoResponseDTO(pedidoAtualizado);
        });
    }
}