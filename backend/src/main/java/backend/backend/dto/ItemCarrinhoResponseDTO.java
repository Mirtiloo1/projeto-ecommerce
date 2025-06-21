package backend.backend.dto;

import lombok.Data;
import backend.backend.model.ItemCarrinho;
import backend.backend.model.Produto;

@Data
public class ItemCarrinhoResponseDTO {

    private Long id;
    private ProdutoResponseDTO produto;
    private Integer quantidade;
    private Double subtotal;

    public ItemCarrinhoResponseDTO(ItemCarrinho itemCarrinho) {
        this.id = itemCarrinho.getId();
        this.quantidade = itemCarrinho.getQuantidade();
        this.subtotal = itemCarrinho.getSubtotal();

        if (itemCarrinho.getProduto() != null) {
            Produto produtoEntity = itemCarrinho.getProduto();
            this.produto = new ProdutoResponseDTO(
                    produtoEntity.getId(),
                    produtoEntity.getNome(),
                    produtoEntity.getDescricao(),
                    produtoEntity.getPreco(),
                    produtoEntity.getQuantidadeEstoque(),
                    produtoEntity.getUrlImagemPrincipal()
            );
        }
    }
}