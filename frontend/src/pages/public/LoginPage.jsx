import { Link } from "react-router-dom";
import { Eye, EyeClosed } from "lucide-react";
import { useState } from "react";

export default function LoginPage() {
  const handleSubmit = (e) => {
    e.preventDefault();
    console.log("Formulário enviado");
  };

  const [showPassword, setShowPassword] = useState(false);
  const togglePassword = () => setShowPassword(!showPassword);

  return (
    <div className="flex flex-col items-center justify-center h-screen bg-gradient-to-br from-neutral-800 to-neutral-900">
      <div className="bg-neutral-200 rounded-2xl border-r-8 border-b-8  border-neutral-500 text-black font-roboto p-8 pt-14 pb-14 flex-col w-full max-w-sm md:max-w-md lg:max-w-md">
        <div className="flex">
          <p className="font-medium text-lg">Logar na sua conta</p>
        </div>

        <p className="font-light text-[14px] text-neutral-800">
          Entre com seu email abaixo para logar.
        </p>

        <form onSubmit={handleSubmit} className="mt-6">
          <div className="mb-8">
            <p className="font-medium text-[15px]">Email</p>
            <input
              type="email"
              placeholder="m@exemplo.com"
              className="w-full bg-neutral-100 h-10 p-4 rounded-lg border-1 border-neutral-400 mt-1"
            />
          </div>

          <div>
            <div className="flex">
              <p className="font-medium text-[15px]">Senha</p>
              <button className="font-medium text-[15px] ml-auto cursor-pointer hover:text-neutral-600 ease-in-out transition duration-100">
                Esqueceu sua senha?
              </button>
            </div>

            <div className="relative">
              {showPassword ? (<Eye onClick={togglePassword} className="absolute top-1/2 right-3 cursor-pointer h-5 w-5 -translate-y-1/2" />) : (<EyeClosed onClick={togglePassword} className="absolute top-1/2 right-3 cursor-pointer h-5 w-5 -translate-y-1/2" />)}
              <input
                type={showPassword ? "text" : "password"}
                className="w-full bg-neutral-100 h-10 p-4 rounded-lg border border-neutral-400 mt-1"
                placeholder="*********"
              />
            </div>

          </div>

          <div className="flex flex-col mt-8 gap-4">
            <button
              type="submit"
              className="w-full bg-neutral-800 hover:bg-neutral-700 ease-in-out transition duration-300 font-medium text-white h-10 rounded-lg cursor-pointer"
            >
              Logar
            </button>
            <button className="w-full bg-neutral-100 hover:bg-white ease-in-out transition duration-100 text-black border font-medium border-neutral-400 h-10 rounded-lg cursor-pointer">
              Logar com Google
            </button>

            <Link to="/register">
              <button className="text-[15px] text-neutral-600 cursor-pointer ease-in-out transition duration-100">
                Não tem conta?{" "}
                <span className="underline hover:text-neutral-500">
                  Registrar
                </span>
              </button>
            </Link>
          </div>
        </form>
      </div>
    </div>
  );
}
