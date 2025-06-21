package backend.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ProdutoRequestDTO {

    @NotBlank(message = "O nome do produto é obrigatório")
    private String nome;

    @NotBlank(message = "A descrição do produto é obrigatória")
    private String descricao;

    @NotNull(message = "O preço do produto é obrigatório")
    @PositiveOrZero(message = "O preço não pode ser negativo")
    private Double preco;

    @NotNull(message = "A quantidade em estoque é obrigatória")
    @PositiveOrZero(message = "A quantidade em estoque não pode ser negativa")
    private Integer quantidadeEstoque;

    @Pattern(regexp = "^(http|https)://.*$", message = "URL da imagem inválida")
    private String urlImagemPrincipal;

}
