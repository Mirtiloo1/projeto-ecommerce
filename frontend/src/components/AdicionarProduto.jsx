import { api } from "@/services/api";
import { useState } from "react";

export default function AdicionarProduto() {
  const [imagem, setImagem] = useState(null);
  const [produto, setProduto] = useState({
    nome: "",
    preco: "",
    descricao: "",
  });

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const formData = new FormData();
      formData.append("file", imagem);
      formData.append("produtoId", "temp");

      const { data: imagemUrl } = await api.post("/upload-imagem", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      // 2. Cria o produto com a URL da imagem
      await api.post("/produtos", {
        ...produto,
        preco: Number(produto.preco),
        urlImagemPrincipal: imagemUrl,
      });

      alert("Produto cadastrado com imagem!");
    } catch (error) {
      console.error("Erro:", error);
    }
  };
  return (
    <form onSubmit={handleSubmit}>
      <input
        type="file"
        onChange={(e) => setImagem(e.target.files[0])}
        accept="image/*"
      />
      <input
        type="text"
        placeholder="Nome"
        value={produto.nome}
        onChange={(e) => setProduto({ ...produto, nome: e.target.value })}
      />
      <input
        type="number"
        placeholder="Preço"
        value={produto.preco}
        onChange={(e) => setProduto({ ...produto, preco: e.target.value })}
      />
      <button type="submit" className="bg-red-500 w-24- h-10 items-center cursor-pointer">Cadastrar</button>
    </form>
  );
}
