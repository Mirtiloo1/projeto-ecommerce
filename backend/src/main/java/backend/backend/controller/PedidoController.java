package backend.backend.controller;

import backend.backend.dto.PedidoResponseDTO;
import backend.backend.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PedidoResponseDTO> criarPedido(
            @RequestParam String enderecoEntrega,
            @RequestParam String formaPagamento) {
        PedidoResponseDTO novoPedido = pedidoService.criarPedidoDoCarrinho(enderecoEntrega, formaPagamento);
        return new ResponseEntity<>(novoPedido, HttpStatus.CREATED); // Retorna 201 Created
    }

    @GetMapping("/meus")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<PedidoResponseDTO>> buscarMeusPedidos(
            @PageableDefault(page = 0, size = 10, sort = "dataHoraPedido", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PedidoResponseDTO> meusPedidos = pedidoService.buscarMeusPedidos(pageable);
        return ResponseEntity.ok(meusPedidos);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PedidoResponseDTO>> buscarTodosPedidos(
            @PageableDefault(page = 0, size = 10, sort = "dataHoraPedido", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PedidoResponseDTO> todosPedidos = pedidoService.buscarTodosPedidos(pageable);
        return ResponseEntity.ok(todosPedidos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PedidoResponseDTO> buscarPedidoPorId(@PathVariable Long id) {
        return pedidoService.buscarPedidoPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PedidoResponseDTO> atualizarStatusPedido(
            @PathVariable Long id,
            @RequestParam String novoStatus) {
        return pedidoService.atualizarStatusPedido(id, novoStatus)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PedidoResponseDTO> cancelarPedido(@PathVariable Long id) {
        return pedidoService.cancelarPedido(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}