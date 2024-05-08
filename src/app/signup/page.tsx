'use client'

import VisibilityIcon from "@mui/icons-material/Visibility";
import VisibilityOffIcon from "@mui/icons-material/VisibilityOff";
import Link from "next/link";
import axios, { AxiosError } from "axios";

import { useState } from "react";
import { API_ENDPOINTS } from "../api";

const SignUp = () => {
    const [showLoginPassword, setShowLoginPassword] = useState(false);

    const handleClickShowPassword = () => {
        setShowLoginPassword(!showLoginPassword);
    };

    const [formData, setFormData] = useState({
        username: "",
        password: "",
        firstName: "",
        lastName: "",
        department: "CS"
    });

    const handleInputChange = (e: { target: { name: any; value: any; }; }) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleDepartmentChange = (e: { target: { value: any; }; }) => {
        setFormData({
            ...formData,
            department: e.target.value
        });
    };

    const handleSubmit = async (e: { preventDefault: () => void; }) => {
        e.preventDefault();
        try {
            const response = await axios.post(API_ENDPOINTS.REGISTER, formData);
            console.log("Registration successful:", response.data);

            const loginResponse = await axios.post(API_ENDPOINTS.LOGIN, {
                username: formData.username,
                password: formData.password
            });

            console.log("Login successful:", loginResponse.data);
            const authToken = loginResponse.data.token;
            localStorage.setItem("token", authToken);

            setFormData({
                username: "",
                password: "",
                firstName: "",
                lastName: "",
                department: "CS"
            });
        } catch (error) {
            const axiosError = error as AxiosError;
            console.error("Registration failed:", axiosError.response?.data);
        }
    };


    return (
        <div className="max-w-[2000px] bg-cover bg-no-repeat bg-center bg-[url('/BG.png')] h-screen box-border px-[5%] py-[5%] flex justify-start mx-auto">
            <div className="flex justify-center w-[43%] h-[33rem] bg-black box-border p-10 rounded-2xl -mt-6">
                <form onSubmit={handleSubmit} method="post" className="flex flex-col items-center w-full h-full bg-customYellow rounded-2xl box-border justify-between	py-10">
                    <div className="flex flex-col items-center">
                        <h1 className="text-3xl font-extrabold font-poppins -mt-7">CREATE AN ACCOUNT!</h1>
                        <p className="text-xs font-light font-poppins mb-2">Create an account to explore exciting events.</p>
                    </div>
                    <div className="flex justify-between gap-10">

                        <div className="w-[63%] -mt-1">

                            <p className="font-poppins text-sm font-regular ml-1 mt-2">First Name<span className="text-red-800">*</span></p>
                            <input
                                type="text"
                                name="firstName"
                                value={formData.firstName}
                                onChange={handleInputChange}
                                placeholder=" Enter First Name"
                                style={{ fontSize: '13px' }}
                                className="w-40 h-[37px] rounded-2xl border-2 ml-1 border-black"
                            />
                        </div>
                        <div>
                            <p className="font-poppins text-sm font-regular -ml-2 mt-1">Last Name<span className="text-red-800">*</span></p>
                            <input
                                type="text"
                                name="lastName"
                                value={formData.lastName}
                                onChange={handleInputChange}
                                placeholder=" Enter Last Name"
                                style={{ fontSize: '13px'}}
                                className="w-40 h-[37px] rounded-2xl border-2 border-black -ml-2" />
                        </div>
                    </div>
                    <div className="w-[60%]">
                        <p className="font-poppins text-sm font-regular mt-3">Username/Email Address<span className="text-red-800">*</span></p>
                        <input
                            type="text"
                            name="username"
                            value={formData.username}
                            onChange={handleInputChange}
                            placeholder=" Enter Username/Email Address"
                            style={{ fontSize: '13px', marginLeft:'2px' }}
                            className="w-full h-[37px] rounded-2xl border-2 border-black" />
                    </div>
                    <div className="w-[60%]">
                        <p className="font-poppins text-sm font-regular mt-2">Department<span className="text-red-800">*</span></p>
                        <select
                            value={formData.department}
                            onChange={handleDepartmentChange}
                            className="w-full h-[37px] rounded-2xl border-2 border-black">
                            <option value="department2">CS</option>
                            <option value="department1">IT</option>
                        </select>
                    </div>
                    <div className="w-[60%]">
                        <p className="font-poppins text-sm font-regular ml-1 mt-2">Password<span className="text-red-800">*</span></p>
                        <div className="relative box-border">
                            <input type={showLoginPassword ? "text" : "password"}
                                name="password"
                                value={formData.password}
                                onChange={handleInputChange}
                                placeholder=" Enter Password"
                                style={{ fontSize: '13px' }}
                                className="pr-8 w-full rounded-2xl h-[37px] border-2 border-black" />
                            <span className="absolute inset-y-0 right-0 flex items-center pr-1">
                                <span className="cursor-pointer -ml-7 -mt-.5" onClick={handleClickShowPassword}>
                                    {showLoginPassword ? <VisibilityIcon /> : <VisibilityOffIcon />}
                                </span>
                            </span>
                        </div>
                        <span className="text-xs flex justify-end font-light mb-2 mt-.5">Forgot Password?</span>
                    </div>
                    <div className="flex flex-col items-center">
                        <button type="submit" className="bg-black text-customYellow w-[110px] h-[35px] text-xl rounded font-bold mb-5 mt-5">SIGN UP</button>
                        <p className="font-light text-xs font-poppins -mt-5">Already have an account? <Link href="/login" replace className="font-bold cursor-pointer">LOGIN</Link></p>
                    </div>
                </form>
            </div>
        </div>
    );
}
export default SignUp;
