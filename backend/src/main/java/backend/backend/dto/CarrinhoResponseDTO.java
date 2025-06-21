package backend.backend.dto;

import lombok.Data;
import backend.backend.model.Carrinho;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CarrinhoResponseDTO {

    private Long id;
    private UsuarioResponseDTO usuario;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private Double valorTotal;

    private List<ItemCarrinhoResponseDTO> itens;

    public CarrinhoResponseDTO(Carrinho carrinho) {
        this.id = carrinho.getId();
        this.dataCriacao = carrinho.getDataCriacao();
        this.dataAtualizacao = carrinho.getDataAtualizacao();
        this.valorTotal = carrinho.getValorTotal();

        if (carrinho.getUsuario() != null) {
            this.usuario = new UsuarioResponseDTO(
                    carrinho.getUsuario().getId(),
                    carrinho.getUsuario().getNome(),
                    carrinho.getUsuario().getEmail(),
                    carrinho.getUsuario().getRoles()
            );
        }

        if (carrinho.getItens() != null && !carrinho.getItens().isEmpty()) {
            this.itens = carrinho.getItens().stream()
                    .map(ItemCarrinhoResponseDTO::new)
                    .collect(Collectors.toList());
        }
    }
}