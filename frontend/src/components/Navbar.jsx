import MenuMobile from "./ui/MenuMobile";
import BotaoBusca from "./ui/Botao-Busca";
import { Menu } from "lucide-react";
import { Link } from "react-router-dom";
import {
  Sheet,
  SheetContent,
  SheetTrigger,
  SheetTitle,
  SheetDescription,
} from "@/components/ui/sheet";

function Navbar() {
  return (
    <nav>
      <div className="w-full bg-white drop-shadow-2xl">
        <div className="container mx-auto flex items-center justify-between px-4 py-4 md:px-8 md:py-8 gap-4 md:gap-0 lg:gap-12 font-roboto">
          <div className="flex">
            <Sheet>
              <SheetTrigger asChild>
                <button
                  className="md:hidden p-1.5 cursor-pointer"
                  aria-label="Abrir menu"
                >
                  <Menu
                    className="w-7 h-7 flex-shrink-0"
                    strokeWidth={1.5}
                    aria-label="Menu"
                  />
                </button>
              </SheetTrigger>

              <SheetContent
                side="left"
                className="bg-white/95 backdrop-blur-sm w-[280px] sm:w-[300px] border-r-0"
              >
                <SheetTitle className="sr-only">Busca</SheetTitle>
                <SheetDescription className="sr-only">
                  Digite para buscar produtos
                </SheetDescription>
                <div className=" px-4">
                  <ul className="flex flex-col gap-1 px-2 py-4 font-roboto">
                    <div className="px-4 py-4 border-b border-gray-100">
                      <Link to="/" className="inline-block">
                        <h2 className="font-shrikhand text-3xl text-gray-800 hover:text-gray-600 transition-colors">
                          Aura
                        </h2>
                      </Link>
                    </div>
                    {["Categorias", "Ofertas", "Lançamentos", "Marcas"].map(
                      (item) => (
                        <li key={item}>
                          <Link
                            to={`/${item.toLowerCase()}`}
                            className="flex items-center px-4 py-3 rounded-lg hover:bg-gray-100 active:bg-gray-200 transition-all"
                          >
                            <span className="text-[15px] font-medium text-gray-700">
                              {item}
                            </span>
                          </Link>
                        </li>
                      )
                    )}
                  </ul>
                </div>
              </SheetContent>
            </Sheet>
          </div>
          <Link to="/">
            <h1 className="font-shrikhand cursor-pointer text-3xl md:text-4xl md:ml-0 lg:ml-4 lg:text-5xl flex-shrink-0">
              Aura
            </h1>
          </Link>

          <ul
            className="
          hidden md:flex gap-6 lg:gap-8 items-center lg:text-[15px] md:text-[14px] flex-shrink-0 sm:pl-10
          [&>li]:cursor-pointer
          [&>li]:whitespace-nowrap
          [&>li]:hover:text-zinc-500
          [&>li]:transition-colors
          [&>li]:ease-in-out
          [&>li]:duration-200"
          >
            <li>Ofertas</li>
            <li>Lançamentos</li>
            <li>Marcas</li>
          </ul>

          <BotaoBusca />
          <MenuMobile />
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
