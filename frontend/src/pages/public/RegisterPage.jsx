import { Link } from "react-router-dom";
import { Eye, EyeClosed } from "lucide-react";
import { useState } from "react";

export default function LoginPage() {
  const handleSubmit = (e) => {
    e.preventDefault();
    console.log("Formulário enviado");
  };

  const [showPassword1, setShowPassword1] = useState(false);
  const [showPassword2, setShowPassword2] = useState(false);

  const togglePassword1 = () => setShowPassword1(!showPassword1);
  const togglePassword2 = () => setShowPassword2(!showPassword2);

  return (
    <div className="flex flex-col items-center justify-center h-screen bg-gradient-to-br from-neutral-800 to-neutral-900">
      <div className="bg-neutral-200 rounded-2xl border-r-8 border-b-8 border-neutral-500 text-black font-roboto p-8 pt-14 pb-14 flex-col w-full max-w-sm md:max-w-md lg:max-w-md">
        <div className="flex">
          <p className="font-medium text-lg">Criar uma conta</p>
        </div>

        <p className="font-light text-[14px] text-neutral-800">
          Preencha os dados abaixo para criar sua conta.
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

          <div className="space-y-6">
            <div>
              <p className="font-medium text-[15px]">Senha</p>
              <div className="relative">
                <input
                  type={showPassword1 ? "text" : "password"}
                  className="w-full bg-neutral-100 h-10 p-4 pr-10 rounded-lg border border-neutral-400 mt-1"
                  placeholder="*********"
                />
                {showPassword1 ? (
                  <Eye
                    onClick={togglePassword1}
                    className="absolute right-3 top-1/2 transform -translate-y-1/2 h-5 w-5 cursor-pointer"
                  />
                ) : (
                  <EyeClosed
                    onClick={togglePassword1}
                    className="absolute right-3 top-1/2 transform -translate-y-1/2 h-5 w-5 cursor-pointer"
                  />
                )}
              </div>
            </div>

            <div>
              <p className="font-medium text-[15px]">Confirme sua senha</p>
              <div className="relative">
                <input
                  type={showPassword2 ? "text" : "password"}
                  className="w-full bg-neutral-100 h-10 p-4 pr-10 rounded-lg border border-neutral-400 mt-1"
                  placeholder="*********"
                />
                {showPassword2 ? (
                  <Eye
                    onClick={togglePassword2}
                    className="absolute right-3 top-1/2 transform -translate-y-1/2 h-5 w-5 cursor-pointer"
                  />
                ) : (
                  <EyeClosed
                    onClick={togglePassword2}
                    className="absolute right-3 top-1/2 transform -translate-y-1/2 h-5 w-5 cursor-pointer"
                  />
                )}
              </div>
            </div>
          </div>

          <div className="flex flex-col mt-8 gap-4">
            <button
              type="submit"
              className="w-full bg-neutral-800 hover:bg-neutral-700 ease-in-out transition duration-300 font-medium text-white h-10 rounded-lg cursor-pointer"
            >
              Cadastrar
            </button>
            <Link to="/login">
              <button className="text-[15px] text-neutral-600 cursor-pointer ease-in-out transition duration-100">
                Já tem um conta?{" "}
                <span className="underline hover:text-neutral-500">Logar</span>
              </button>
            </Link>
          </div>
        </form>
      </div>
    </div>
  );
}
