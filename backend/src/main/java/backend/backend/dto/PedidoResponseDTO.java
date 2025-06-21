package backend.backend.dto;

import lombok.Data;
import backend.backend.model.Pedido;
import backend.backend.model.Usuario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PedidoResponseDTO {

    private Long id;
    private LocalDateTime dataHoraPedido;
    private String status;
    private Double valorTotal;

    private UsuarioResponseDTO usuario;
    private List<ItemPedidoResponseDTO> itens;

    public PedidoResponseDTO(Pedido pedido) {
        this.id = pedido.getId();
        this.dataHoraPedido = pedido.getDataHoraPedido();
        this.status = pedido.getStatus();
        this.valorTotal = pedido.getValorTotal();

        if (pedido.getUsuario() != null) {
            Usuario usuarioEntity = pedido.getUsuario();
            this.usuario = new UsuarioResponseDTO(
                    usuarioEntity.getId(),
                    usuarioEntity.getNome(),
                    usuarioEntity.getEmail(),
                    usuarioEntity.getRoles()
            );
        }

        if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
            this.itens = pedido.getItens().stream()
                    .map(ItemPedidoResponseDTO::new)
                    .collect(Collectors.toList());
        }
    }
}