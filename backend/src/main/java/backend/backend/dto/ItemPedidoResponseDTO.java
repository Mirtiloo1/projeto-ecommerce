package backend.backend.dto;

import lombok.Data;
import backend.backend.model.ItemPedido;
import backend.backend.model.Produto;

@Data
public class ItemPedidoResponseDTO {

    private Long id;
    private ProdutoResponseDTO produto;
    private Integer quantidade;
    private Double subtotal;

    public ItemPedidoResponseDTO(ItemPedido itemPedido) {
        this.id = itemPedido.getId();
        this.quantidade = itemPedido.getQuantidade();
        this.subtotal = itemPedido.getPrecoUnitario() * itemPedido.getQuantidade();

        if (itemPedido.getProduto() != null) {
            Produto produto = itemPedido.getProduto();
            this.produto = new ProdutoResponseDTO(
                    produto.getId(),
                    produto.getNome(),
                    produto.getDescricao(),
                    produto.getPreco(),
                    produto.getQuantidadeEstoque(),
                    produto.getUrlImagemPrincipal()
            );
        }
    }
}