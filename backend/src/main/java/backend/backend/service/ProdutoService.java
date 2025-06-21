package backend.backend.service;

import backend.backend.model.Produto;
import backend.backend.repository.ProdutoRepository;
import backend.backend.dto.ProdutoRequestDTO;
import backend.backend.dto.ProdutoResponseDTO;
import backend.backend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Transactional
    public ProdutoResponseDTO criarProduto(ProdutoRequestDTO produtoRequestDTO) {

        Produto produto = new Produto();
        produto.setNome(produtoRequestDTO.getNome());
        produto.setDescricao(produtoRequestDTO.getDescricao());
        produto.setPreco(produtoRequestDTO.getPreco());
        produto.setQuantidadeEstoque(produtoRequestDTO.getQuantidadeEstoque());
        produto.setUrlImagemPrincipal(produtoRequestDTO.getUrlImagemPrincipal());

        Produto produtoSalvo = produtoRepository.save(produto);

        return new ProdutoResponseDTO(
                produtoSalvo.getId(),
                produtoSalvo.getNome(),
                produtoSalvo.getDescricao(),
                produtoSalvo.getPreco(),
                produtoSalvo.getQuantidadeEstoque(),
                produtoSalvo.getUrlImagemPrincipal()
        );
    }

    public Page<ProdutoResponseDTO> buscarTodosProdutos(String nome, Double precoMin, Double precoMax, Pageable pageable) {
        Specification<Produto> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nome != null && !nome.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nome")), "%" + nome.toLowerCase() + "%"));
            }
            if (precoMin != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("preco"), precoMin));
            }
            if (precoMax != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("preco"), precoMax));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Produto> produtosPage = produtoRepository.findAll(spec, pageable);

        return produtosPage.map(produto -> new ProdutoResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getQuantidadeEstoque(),
                produto.getUrlImagemPrincipal()
        ));
    }

    public ProdutoResponseDTO buscarProdutoPorId(Long id) { // Retornando diretamente o DTO ou lançando exceção
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));

        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getQuantidadeEstoque(),
                produto.getUrlImagemPrincipal()
        );
    }

    @Transactional
    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoRequestDTO produtoRequestDTO) {
        Produto produtoExistente = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));

        produtoExistente.setNome(produtoRequestDTO.getNome());
        produtoExistente.setDescricao(produtoRequestDTO.getDescricao());
        produtoExistente.setPreco(produtoRequestDTO.getPreco());
        produtoExistente.setQuantidadeEstoque(produtoRequestDTO.getQuantidadeEstoque());
        produtoExistente.setUrlImagemPrincipal(produtoRequestDTO.getUrlImagemPrincipal());

        Produto produtoAtualizado = produtoRepository.save(produtoExistente);

        return new ProdutoResponseDTO(
                produtoAtualizado.getId(),
                produtoAtualizado.getNome(),
                produtoAtualizado.getDescricao(),
                produtoAtualizado.getPreco(),
                produtoAtualizado.getQuantidadeEstoque(),
                produtoAtualizado.getUrlImagemPrincipal()
        );
    }

    @Transactional
    public void deletarProduto(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produto não encontrado com ID: " + id);
        }
        produtoRepository.deleteById(id);
    }
}