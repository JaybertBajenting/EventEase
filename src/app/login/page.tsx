'use client'


import VisibilityIcon from "@mui/icons-material/Visibility";
import VisibilityOffIcon from "@mui/icons-material/VisibilityOff";
import axios, { AxiosError } from "axios";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { API_ENDPOINTS } from "../api";

const Login = () => {
    const router = useRouter();

    const [showLoginPassword, setShowLoginPassword] = useState(false);
    const [validEmail, setValidEmail] = useState(true); // State to track email validity

    const handleClickShowPassword = () => {
        setShowLoginPassword(!showLoginPassword);
    };

    const [formData, setFormData] = useState({
        username: "",
        password: "",
    });

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });

        // Check email validity
        if (e.target.name === "username") {
            setValidEmail(validateEmail(e.target.value));
        }
    };

    const validateEmail = (email: string) => {
        // Regular expression for email validation
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    };

    const handleLoginSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (!validEmail) {
            // If email is not valid, prevent form submission
            return;
        }
        try {
            const response = await axios.post(API_ENDPOINTS.LOGIN, formData);
            console.log("Login successful:", response.data);
            const authToken = response.data.token;
            const role = response.data.user.role;
            const name = response.data.user.name;
            localStorage.setItem("token", authToken);
            localStorage.setItem("role", role);
            localStorage.setItem("name", name);
            setFormData({
                username: "",
                password: "",
            });
            router.push('/dashboard')
        } catch (error) {
            const axiosError = error as AxiosError;
            console.error("Login failed:", axiosError.response?.data);
        }
    };


    return (
        <div className="max-w-[2000px] bg-cover bg-no-repeat bg-center bg-[url('/BG.png')] h-screen box-border px-[5%] py-[5%] flex lg:justify-end mx-auto justify-center">
            <div className="flex justify-center w-[100%] md:w-[40%] lg:w-[30%] h-[60vh] bg-black box-border lg:p-10 p-5 rounded-2xl mt-6 md:mt-12 lg:mt-16">
                <form onSubmit={handleLoginSubmit} method="post" className="flex flex-col items-center w-full h-full bg-customYellow rounded-2xl box-border justify-between py-10">
                    <div className="flex flex-col items-center">
                        <h1 className="text-4xl font-extrabold font-poppins -mt-7">WELCOME!</h1>
                        <p className="text-xs font-light font-poppins mb-2">Please enter your Login and Password.</p>
                    </div>
                    <div className="w-[63%] md:w-[75%] lg:w-[85%]">
                        <p className="font-poppins text-sm font-regular  -mt-8">Username/Email Address<span className="text-red-800">*</span></p>
                        <input
                            type="text"
                            name="username"
                            value={formData.username}
                            onChange={handleInputChange}
                            placeholder=" Enter Username/Email Address"
                            style={{ fontSize: '13px', marginLeft: '2px', borderColor: validEmail ? 'black' : 'red' }} // Update border color based on email validity
                            className="w-full h-[37px] rounded-2xl border-2" // Removed 'border-black'
                        />
                        {!validEmail && <p className="text-red-800 text-xs mt-1">Please enter a valid email address.</p>} {/* Render alert message if email is not valid */}
                    </div>
                    <div className="w-[63%] md:w-[75%] lg:w-[85%]">
                        <p className="font-poppins text-sm font-regular -mt-8">Password<span className="text-red-800">*</span></p>
                        <div className="relative box-border">
                            <input
                                type={showLoginPassword ? "text" : "password"}
                                name="password"
                                value={formData.password}
                                onChange={handleInputChange}
                                placeholder=" Enter Password"
                                style={{ fontSize: '13px', marginLeft: '2px' }}
                                className="pr-8 w-full rounded-2xl h-[37px] border-2 border-black"
                            />
                            <span className="absolute inset-y-0 right-0 flex items-center pr-1">
                                <span className="cursor-pointer -ml-7 -mt-.5" onClick={handleClickShowPassword}>
                                    {showLoginPassword ? <VisibilityIcon /> : <VisibilityOffIcon />}
                                </span>
                            </span>
                        </div>
                        <span className="text-xs flex justify-end font-light mb-2 mt-.5">Forgot Password?</span>
                    </div>
                    <div className="flex flex-col items-center">
                        <button type="submit" className="bg-black text-customYellow w-[110px] h-[35px] text-xl rounded font-bold mb-5 -mt-4">LOGIN</button>
                        <p className="font-light text-xs font-poppins -mt-5">Don't have an account? <Link href="/signup" replace className="font-bold cursor-pointer mt-5">SIGN IN</Link></p>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default Login;
