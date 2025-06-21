package backend.backend.service;

import backend.backend.model.Carrinho;
import backend.backend.model.ItemCarrinho;
import backend.backend.model.Produto;
import backend.backend.model.Usuario;
import backend.backend.repository.CarrinhoRepository;
import backend.backend.repository.ItemCarrinhoRepository;
import backend.backend.repository.ProdutoRepository;
import backend.backend.repository.UsuarioRepository;
import backend.backend.dto.CarrinhoResponseDTO;
import backend.backend.dto.ItemCarrinhoRequestDTO;
import backend.backend.exception.BadRequestException; // Importe a exceção
import backend.backend.exception.ResourceNotFoundException; // Importe a exceção
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CarrinhoService {

    @Autowired
    private CarrinhoRepository carrinhoRepository;
    @Autowired
    private ItemCarrinhoRepository itemCarrinhoRepository;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("Usuário não autenticado.");
        }
        String emailUsuario = ((UserDetails) authentication.getPrincipal()).getUsername();
        return usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário logado não encontrado no banco de dados."));
    }

    @Transactional
    public CarrinhoResponseDTO getOrCreateCarrinhoUsuario() {
        Usuario usuario = getUsuarioLogado();
        Optional<Carrinho> carrinhoOptional = carrinhoRepository.findByUsuario(usuario);

        Carrinho carrinho;
        if (carrinhoOptional.isPresent()) {
            carrinho = carrinhoOptional.get();
        } else {
            carrinho = new Carrinho();
            carrinho.setUsuario(usuario);
            carrinhoRepository.save(carrinho);
        }
        return new CarrinhoResponseDTO(carrinho);
    }

    @Transactional
    public CarrinhoResponseDTO adicionarItemAoCarrinho(ItemCarrinhoRequestDTO itemCarrinhoDTO) {
        Carrinho carrinho = carrinhoRepository.findByUsuario(getUsuarioLogado())
                .orElseGet(() -> {
                    Carrinho novoCarrinho = new Carrinho();
                    novoCarrinho.setUsuario(getUsuarioLogado());
                    return carrinhoRepository.save(novoCarrinho);
                });

        Produto produto = produtoRepository.findById(itemCarrinhoDTO.getProdutoId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + itemCarrinhoDTO.getProdutoId()));

        Optional<ItemCarrinho> itemExistenteOptional = itemCarrinhoRepository.findByCarrinhoAndProduto(carrinho, produto);

        ItemCarrinho itemCarrinho;
        if (itemExistenteOptional.isPresent()) {
            itemCarrinho = itemExistenteOptional.get();
            itemCarrinho.setQuantidade(itemCarrinho.getQuantidade() + itemCarrinhoDTO.getQuantidade());
        } else {
            itemCarrinho = new ItemCarrinho();
            itemCarrinho.setCarrinho(carrinho);
            itemCarrinho.setProduto(produto);
            itemCarrinho.setQuantidade(itemCarrinhoDTO.getQuantidade());
            carrinho.adicionarItem(itemCarrinho);
        }

        if (itemCarrinho.getQuantidade() > produto.getQuantidadeEstoque()) {
            throw new BadRequestException("Quantidade em estoque insuficiente para o produto: " + produto.getNome() + ". Estoque disponível: " + produto.getQuantidadeEstoque());
        }

        itemCarrinhoRepository.save(itemCarrinho);
        return new CarrinhoResponseDTO(carrinho);
    }

    @Transactional
    public CarrinhoResponseDTO removerItemDoCarrinho(Long itemId) {
        Carrinho carrinho = carrinhoRepository.findByUsuario(getUsuarioLogado())
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado para o usuário."));

        ItemCarrinho itemParaRemover = itemCarrinhoRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item do carrinho não encontrado com ID: " + itemId));

        if (!itemParaRemover.getCarrinho().getId().equals(carrinho.getId())) {
            throw new BadRequestException("Este item não pertence ao carrinho do usuário logado.");
        }

        carrinho.removerItem(itemParaRemover);
        itemCarrinhoRepository.delete(itemParaRemover);
        return new CarrinhoResponseDTO(carrinho);
    }

    @Transactional
    public CarrinhoResponseDTO atualizarQuantidadeItemCarrinho(Long itemId, Integer novaQuantidade) {
        if (novaQuantidade <= 0) {
            throw new BadRequestException("A quantidade deve ser maior que zero. Use removerItemDoCarrinho para remover.");
        }

        Carrinho carrinho = carrinhoRepository.findByUsuario(getUsuarioLogado())
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado para o usuário."));

        ItemCarrinho itemParaAtualizar = itemCarrinhoRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item do carrinho não encontrado com ID: " + itemId));

        if (!itemParaAtualizar.getCarrinho().getId().equals(carrinho.getId())) {
            throw new BadRequestException("Este item não pertence ao carrinho do usuário logado.");
        }

        Produto produto = itemParaAtualizar.getProduto();
        if (novaQuantidade > produto.getQuantidadeEstoque()) {
            throw new BadRequestException("Quantidade em estoque insuficiente para o produto: " + produto.getNome() + ". Estoque disponível: " + produto.getQuantidadeEstoque());
        }

        itemParaAtualizar.setQuantidade(novaQuantidade);
        itemCarrinhoRepository.save(itemParaAtualizar);
        return new CarrinhoResponseDTO(carrinho);
    }

    @Transactional
    public CarrinhoResponseDTO limparCarrinho() {
        Carrinho carrinho = carrinhoRepository.findByUsuario(getUsuarioLogado())
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado para o usuário."));

        itemCarrinhoRepository.deleteAll(carrinho.getItens());
        carrinho.getItens().clear();

        carrinhoRepository.save(carrinho);
        return new CarrinhoResponseDTO(carrinho);
    }
}