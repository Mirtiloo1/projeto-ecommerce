package backend.backend.controller;

import backend.backend.dto.CarrinhoResponseDTO;
import backend.backend.dto.ItemCarrinhoRequestDTO;
import backend.backend.service.CarrinhoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrinho")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;

    @GetMapping
    public ResponseEntity<CarrinhoResponseDTO> getCarrinhoUsuario() {
        CarrinhoResponseDTO carrinho = carrinhoService.getOrCreateCarrinhoUsuario();
        return ResponseEntity.ok(carrinho);
    }

    @PostMapping("/adicionar")
    public ResponseEntity<CarrinhoResponseDTO> adicionarItemAoCarrinho(@Valid @RequestBody ItemCarrinhoRequestDTO itemCarrinhoDTO) {
        CarrinhoResponseDTO carrinhoAtualizado = carrinhoService.adicionarItemAoCarrinho(itemCarrinhoDTO);
        return new ResponseEntity<>(carrinhoAtualizado, HttpStatus.OK);
    }

    @DeleteMapping("/remover/{itemId}")
    public ResponseEntity<CarrinhoResponseDTO> removerItemDoCarrinho(@PathVariable Long itemId) {
        CarrinhoResponseDTO carrinhoAtualizado = carrinhoService.removerItemDoCarrinho(itemId);
        return ResponseEntity.ok(carrinhoAtualizado);
    }

    @PutMapping("/atualizar/{itemId}/{novaQuantidade}")
    public ResponseEntity<CarrinhoResponseDTO> atualizarQuantidadeItemCarrinho(
            @PathVariable Long itemId,
            @PathVariable Integer novaQuantidade) {
        CarrinhoResponseDTO carrinhoAtualizado = carrinhoService.atualizarQuantidadeItemCarrinho(itemId, novaQuantidade);
        return ResponseEntity.ok(carrinhoAtualizado);
    }

    @DeleteMapping("/limpar")
    public ResponseEntity<CarrinhoResponseDTO> limparCarrinho() {
        CarrinhoResponseDTO carrinhoLimpo = carrinhoService.limparCarrinho();
        return ResponseEntity.ok(carrinhoLimpo);
    }
}