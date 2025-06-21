package backend.backend.dto;

import lombok.Data;

@Data
public class ProdutoResponseDTO {

    private Long id;
    private String nome;
    private String descricao;
    private Double preco;
    private Integer quantidadeEstoque;
    private String urlImagemPrincipal;

    public ProdutoResponseDTO(Long id, String nome, String descricao, Double preco, Integer quantidadeEstoque, String urlImagemPrincipal) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.urlImagemPrincipal = urlImagemPrincipal;
    }
}
