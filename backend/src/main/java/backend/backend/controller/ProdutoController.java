package backend.backend.controller;

import backend.backend.dto.ProdutoRequestDTO;
import backend.backend.dto.ProdutoResponseDTO;
import backend.backend.exception.ResourceNotFoundException;
import backend.backend.model.Produto;
import backend.backend.repository.ProdutoRepository;
import backend.backend.service.ProdutoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    // Adicionando um logger para vermos o erro completo no console
    private static final Logger logger = LoggerFactory.getLogger(ProdutoController.class);

    private final ProdutoService produtoService;
    private final ProdutoRepository produtoRepository;
    private final Path uploadDir;

    @Autowired
    public ProdutoController(ProdutoService produtoService, ProdutoRepository produtoRepository) {
        this.produtoService = produtoService;
        this.produtoRepository = produtoRepository;

        // Forma mais robusta de definir o caminho do diretório
        this.uploadDir = Paths.get(System.getProperty("user.dir"), "backend", "uploads").toAbsolutePath();

        try {
            // Garante que o diretório de upload exista
            Files.createDirectories(uploadDir);
            logger.info("Diretório de upload configurado em: {}", uploadDir.toString());
        } catch (IOException e) {
            // Se não conseguir criar o diretório, a aplicação não deve subir
            throw new RuntimeException("Não foi possível criar o diretório de upload: " + uploadDir.toString(), e);
        }
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criarProduto(@Valid @RequestBody ProdutoRequestDTO produtoRequestDTO) {
        ProdutoResponseDTO novoProduto = produtoService.criarProduto(produtoRequestDTO);
        return new ResponseEntity<>(novoProduto, HttpStatus.CREATED);
    }

    @PostMapping("/upload-imagem")
    public ResponseEntity<String> uploadImagemProduto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("produtoId") Long produtoId) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Arquivo vazio. Por favor, envie um arquivo.");
        }
        try {
            Produto produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + produtoId));

            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String nomeArquivo = "produto-" + produtoId + "-" + System.currentTimeMillis() + "." + extension;
            Path destinoArquivo = this.uploadDir.resolve(nomeArquivo);

            // Salva o arquivo na pasta local
            Files.copy(file.getInputStream(), destinoArquivo);

            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(nomeArquivo)
                    .toUriString();

            produto.setUrlImagemPrincipal(fileDownloadUri);
            produtoRepository.save(produto);

            return ResponseEntity.ok(fileDownloadUri);
        } catch (Exception e) {
            // Log do erro completo no console do backend
            logger.error("Falha ao fazer upload do arquivo. Causa: ", e);
            return ResponseEntity.status(500).body("Erro no servidor ao tentar salvar o arquivo.");
        }
    }

    @GetMapping
    public ResponseEntity<Page<ProdutoResponseDTO>> buscarTodosProdutos(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Double precoMin,
            @RequestParam(required = false) Double precoMax,
            @PageableDefault(page = 0, size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ProdutoResponseDTO> produtos = produtoService.buscarTodosProdutos(nome, precoMin, precoMax, pageable);
        return ResponseEntity.ok(produtos);
    }
}