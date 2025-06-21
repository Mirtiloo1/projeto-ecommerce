import { useEffect, useState } from "react";
import Navbar from "../../components/Navbar";
import { getProdutos } from "@/services/api";
import AdicionarProduto from "@/components/AdicionarProduto";

function HomePage() {
  const [produtos, setProdutos] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const carregarProdutos = async () => {
      try {
        const dados = await getProdutos();
        setProdutos(dados);
      } catch (error) {
        console.error("Falha ao carregar produtos:", error);
      } finally {
        setLoading(false);
      }
    };
    carregarProdutos();
  }, []);

  if (loading) {
    return (
      <div className="font-roboto min-h-screen flex flex-col">
        <Navbar />
        <div className="flex-1 flex items-center justify-center">
          <p>Carregando produtos...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="font-roboto min-h-screen flex flex-col">
      <Navbar />

      <AdicionarProduto />

      <main className="flex-1">
        <div className="container px-4 sm:px-6 lg:px-8 py-8 mx-auto">
          <div className="grid grid-cols-1 xs:grid-cols-2 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 2xl:grid-cols-6 gap-4 sm:gap-6 md:gap-8">
            {produtos.map((produto) => (
              <div
                key={produto.id}
                className="aspect-square bg-white rounded-lg shadow-sm hover:shadow-md transition-shadow duration-300 p-4"
              >
                <img
                  src={produto.urlImagemPrincipal}
                  alt={produto.nome}
                  className="w-full h-48 object-cover mb-2"
                  onError={(e) => {
                    e.target.src = "https://placehold.co/300";
                  }}
                />
                <div className="mt-2">
                  <h3 className="font-medium text-sm truncate">
                    {produto.nome}
                  </h3>
                  <p className="text-neutral-600 font-bold">
                    R$ {produto.preco.toFixed(2)}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </main>
    </div>
  );
}

export default HomePage;
