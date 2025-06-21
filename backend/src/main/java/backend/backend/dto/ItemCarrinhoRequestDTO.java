package backend.backend.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
public class ItemCarrinhoRequestDTO {

    @NotNull(message = "O ID do produto é obrigatório.")
    private Long produtoId;

    @Min(value = 1, message = "A quantidade deve ser no mínimo 1.")
    private Integer quantidade;
}