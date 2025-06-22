import { api } from "@/services/api";
import { useState } from "react";

// 1. Recebemos a propriedade { onProdutoAdicionado }
export default function AdicionarProduto({ onProdutoAdicionado }) {
  const [imagem, setImagem] = useState(null);
  const [produto, setProduto] = useState({
    nome: "",
    preco: "",
    descricao: "",
    quantidadeEstoque: "",
  });
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!imagem) {
      alert("Por favor, selecione uma imagem.");
      return;
    }
    setLoading(true);

    try {
      const { data: produtoCriado } = await api.post("/produtos", {
        nome: produto.nome,
        preco: Number(produto.preco),
        descricao: produto.descricao,
        quantidadeEstoque: Number(produto.quantidadeEstoque),
      });

      const produtoId = produtoCriado.id;

      const formData = new FormData();
      formData.append("file", imagem);
      formData.append("produtoId", produtoId);

      await api.post("/produtos/upload-imagem", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      alert("Produto cadastrado e imagem enviada com sucesso!");

      // 2. CHAMAMOS A FUNÇÃO DO PAI PARA ATUALIZAR A LISTA DE PRODUTOS
      if (onProdutoAdicionado) {
        onProdutoAdicionado();
      }

      e.target.reset();
      setProduto({ nome: "", preco: "", descricao: "", quantidadeEstoque: "" });
      // Limpa o estado da imagem também, se necessário
      setImagem(null);
    } catch (error) {
      console.error("Erro no processo de cadastro:", error);

      let errorMessage = "Falha ao cadastrar produto.";
      if (error.response && error.response.data) {
        const errorData = error.response.data;
        errorMessage += `\n${errorData.message || ""}`;
        if (errorData.details && Array.isArray(errorData.details)) {
          errorMessage += `\nDetalhes: ${errorData.details.join(", ")}`;
        }
      }
      alert(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-4 p-4">
      <input
        type="text"
        placeholder="Nome"
        value={produto.nome}
        onChange={(e) => setProduto({ ...produto, nome: e.target.value })}
        required
      />
      <input
        type="number"
        placeholder="Preço"
        value={produto.preco}
        onChange={(e) => setProduto({ ...produto, preco: e.target.value })}
        required
        step="0.01" // Boa prática para preços
      />
      <textarea
        placeholder="Descrição"
        value={produto.descricao}
        onChange={(e) => setProduto({ ...produto, descricao: e.target.value })}
        required
      />
      <input
        type="number"
        placeholder="Quantidade em Estoque"
        value={produto.quantidadeEstoque}
        onChange={(e) =>
          setProduto({ ...produto, quantidadeEstoque: e.target.value })
        }
        required
      />
      <input
        type="file"
        // Adicionar um 'key' que muda quando limpamos o formulário
        // é um truque para resetar o input de arquivo
        key={imagem ? "com-imagem" : "sem-imagem"}
        onChange={(e) => setImagem(e.target.files[0])}
        accept="image/*"
        required
      />
      <button
        type="submit"
        disabled={loading}
        className="bg-red-500 w-full h-10 items-center cursor-pointer disabled:bg-gray-400"
      >
        {loading ? "Cadastrando..." : "Cadastrar"}
      </button>
    </form>
  );
}
