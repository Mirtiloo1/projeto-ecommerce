package backend.backend.controller;

import backend.backend.dto.ProdutoRequestDTO;
import backend.backend.dto.ProdutoResponseDTO;
import backend.backend.exception.ResourceNotFoundException;
import backend.backend.model.Produto;
import backend.backend.repository.ProdutoRepository;
import backend.backend.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final S3Client s3Client;
    private final ProdutoRepository produtoRepository;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    @Autowired
    public ProdutoController(ProdutoService produtoService, S3Client s3Client, ProdutoRepository produtoRepository) {
        this.produtoService = produtoService;
        this.s3Client = s3Client;
        this.produtoRepository = produtoRepository;
    }

    private String uploadToS3(MultipartFile file, String key) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criarProduto(@Valid @RequestBody ProdutoRequestDTO produtoRequestDTO) {
        ProdutoResponseDTO novoProduto = produtoService.criarProduto(produtoRequestDTO);
        return new ResponseEntity<>(novoProduto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<ProdutoResponseDTO>> buscarTodosProdutos(@RequestParam(required = false) String nome, @RequestParam(required = false) Double precoMin, @RequestParam(required = false) Double precoMax, @PageableDefault(page = 0, size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ProdutoResponseDTO> produtos = produtoService.buscarTodosProdutos(nome, precoMin, precoMax, pageable);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarProdutoPorId(@PathVariable Long id) {
        ProdutoResponseDTO produto = produtoService.buscarProdutoPorId(id);
        return ResponseEntity.ok(produto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizarProduto(@PathVariable Long id, @Valid @RequestBody ProdutoRequestDTO produtoRequestDTO) {
        ProdutoResponseDTO produtoAtualizado = produtoService.atualizarProduto(id, produtoRequestDTO);
        return ResponseEntity.ok(produtoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        produtoService.deletarProduto(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/upload-imagem")
    public ResponseEntity<String> uploadImagemProduto(@RequestParam("file") MultipartFile file, @RequestParam("produtoId") Long produtoId) {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("Arquivo vazio");
        if (!file.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().body("Apenas imagens são permitidas");
        }
        try {
            Produto produto = produtoRepository.findById(produtoId).orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + produtoId));
            String fileName = "produtos/produto-" + produtoId + "-" + System.currentTimeMillis() + "." + StringUtils.getFilenameExtension(file.getOriginalFilename());
            String fileUrl = uploadToS3(file, fileName);
            produto.setUrlImagemPrincipal(fileUrl);
            produtoRepository.save(produto);
            return ResponseEntity.ok(fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro no upload: " + e.getMessage());
        }
    }
}