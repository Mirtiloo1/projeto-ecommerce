package backend.backend.controller;

import backend.backend.dto.ProdutoRequestDTO;
import backend.backend.dto.ProdutoResponseDTO;
import backend.backend.service.ProdutoService;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/api/produtos")
@EnableMethodSecurity
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 s3Client;

    @Autowired
    public ProdutoController(ProdutoService produtoService, AmazonS3 s3Client) {
        this.produtoService = produtoService;
        this.s3Client = s3Client;
    }

    private String uploadToS3(MultipartFile file, String fileName) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        s3Client.putObject(
                new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata)
        );

        return s3Client.getUrl(bucketName, fileName).toString();
    }
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutoResponseDTO> criarProduto(
            @Valid @RequestBody ProdutoRequestDTO produtoRequestDTO,
            @RequestParam(required = false) String imagemUrl) {

        if (imagemUrl != null) {
            produtoRequestDTO.setUrlImagemPrincipal(imagemUrl);
        }

        ProdutoResponseDTO novoProduto = produtoService.criarProduto(produtoRequestDTO);
        return new ResponseEntity<>(novoProduto, HttpStatus.CREATED);
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

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarProdutoPorId(@PathVariable Long id) {
        ProdutoResponseDTO produto = produtoService.buscarProdutoPorId(id);
        return ResponseEntity.ok(produto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutoResponseDTO> atualizarProduto(@PathVariable Long id, @Valid @RequestBody ProdutoRequestDTO produtoRequestDTO) {
        ProdutoResponseDTO produtoAtualizado = produtoService.atualizarProduto(id, produtoRequestDTO);
        return ResponseEntity.ok(produtoAtualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        produtoService.deletarProduto(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/upload-imagem")
    public ResponseEntity<String> uploadImagemProduto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("produtoId") Long produtoId) {

        if (file.isEmpty()) return ResponseEntity.badRequest().body("Arquivo vazio");
        if (!file.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().body("Apenas imagens são permitidas");
        }

        try {
            String fileName = "produto-" + produtoId + "-" + System.currentTimeMillis() + "." +
                    StringUtils.getFilenameExtension(file.getOriginalFilename());

            String fileUrl = uploadToS3(file, "produtos/" + fileName);

            return ResponseEntity.ok(fileUrl);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro no upload: " + e.getMessage());
        }
    }
}