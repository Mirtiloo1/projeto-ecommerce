import { Search } from "lucide-react";

export default function BotaoBusca() {
  return (
    <div className="hidden md:flex items-center flex-1 min-w-0 ml-4 md:ml-6 md:mr-4 lg:mr-0 lg:ml-0">
      <div className="relative flex-1 max-w-2xl">
        <Search className="absolute left-6 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-500" />
        <input
          type="text"
          placeholder="Buscar produtos..."
          className="pl-14 pr-4 py-2 bg-gray-200 h-10 md:h-10 md:rounded-xl lg:rounded-xl md:text-[13px] lg:text-[15px] sm:h-6 sm:w-full lg:h-12 2xl:w-3xl rounded-2xl border-none focus:outline-none focus-visible:outline focus-visible:outline-blue-500"
        />
      </div>
    </div>
  );
}
