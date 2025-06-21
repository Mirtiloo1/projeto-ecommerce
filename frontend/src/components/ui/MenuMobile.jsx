import { Search, ShoppingCart, CircleUserRound } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Link } from "react-router-dom";
import {
  Sheet,
  SheetContent,
  SheetTrigger,
  SheetTitle,
  SheetDescription,
} from "@/components/ui/sheet";

export default function MenuMobile() {
  return (
    <div className="flex items-center ml-auto">
      {/* Ícones do lado direito */}
      <div className="flex items-center gap-4 sm:gap-6 md:gap-5 lg:gap-4">
        <Sheet>
          <SheetTrigger asChild>
            <button
              className="md:hidden p-1.5 cursor-pointer"
              aria-label="Abrir menu"
            >
              <Search
                className="md:hidden cursor-pointer"
                aria-label="Buscar"
              />
            </button>
          </SheetTrigger>

          <SheetContent
            side="top"
            className="h-32 bg-white/95 backdrop-blur-sm border-none shadow-lg"
          >
            <SheetTitle className="sr-only">Busca</SheetTitle>
            <SheetDescription className="sr-only">
              Digite para buscar produtos
            </SheetDescription>
            <div className="flex justify-center items-center h-full pr-14 pl-14">
              <div className="relative w-full max-w-md mx-auto">
                <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-500" />
                <input
                  autoFocus
                  type="text"
                  placeholder="Buscar produtos..."
                  className="pl-12 pr-4 py-3 h-14 text-base w-full rounded-xl border-none outline-none bg-gray-200 placeholder-transparent peer"
                />
                <label className="absolute left-12 top-1/2 -translate-y-1/2 text-gray-400 peer-focus:hidden">
                  Buscar produtos...
                </label>
              </div>
            </div>
          </SheetContent>
        </Sheet>

        <div className="relative flex-shrink-0 p-2 rounded-xl hover:bg-gray-200 transition-colors ease-in-out duration-300">
          <ShoppingCart
            className="md:w-6 md:h-6 lg:w-8 lg:h-8 cursor-pointer"
            aria-label="Carrinho"
          />
          <Badge className="absolute top-2 right-2 translate-x-1/2 -translate-y-1/2 rounded-full px-1.5 py-0.5 text-[9px] md:text-[11px] lg:text-[11px] bg-neutral-800 text-white">
            3
          </Badge>
        </div>

        {/* Ícone de usuário */}
        <Link
          to="/login"
          aria-label="Login"
          className="flex-shrink-0 p-2 rounded-xl hover:bg-gray-200 transition-colors ease-in-out duration-300"
        >
          <CircleUserRound className="md:w-6 md:h-6 lg:w-8 lg:h-8 cursor-pointer" />
        </Link>
      </div>
    </div>
  );
}
