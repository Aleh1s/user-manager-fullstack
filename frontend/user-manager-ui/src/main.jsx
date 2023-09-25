import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import {ChakraProvider} from "@chakra-ui/react";
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import {createStandaloneToast} from '@chakra-ui/react'
import SignIn from "./components/login/SignIn.jsx";
import AuthProvider from "./components/context/AuthContext.jsx";
import ProtectedRoute from "./components/shared/ProtectedRoute.jsx";
import SignUp from "./components/registration/SignUp.jsx";
import AboutUs from "./components/aboutus/AboutUs.jsx";
import RedirectHandler from "./components/oauth2/RedirectHandler.jsx";

const {ToastContainer} = createStandaloneToast()
const router = createBrowserRouter([
    {
        path: "/",
        element: <SignIn/>
    },
    {
        path: "/registration",
        element: <SignUp/>
    },
    {
        path: "/oauth2/redirect",
        element: <RedirectHandler/>
    },
    {
        path: "/dashboard",
        element: <ProtectedRoute><App/></ProtectedRoute>
    },
    {
        path: "/about-us",
        element: <ProtectedRoute><AboutUs/></ProtectedRoute>
    }
])

ReactDOM.createRoot(document.getElementById('root')).render(
    <React.StrictMode>
        <ChakraProvider>
            <AuthProvider>
                <RouterProvider router={router}/>
            </AuthProvider>
            <ToastContainer/>
        </ChakraProvider>
    </React.StrictMode>,
)
