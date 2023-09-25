import {useNavigate, useSearchParams} from "react-router-dom";
import {useEffect} from "react";
import {useAuth} from "../context/AuthContext.jsx";
import {errorNotification, successNotification} from "../../services/notification.js";

const RedirectHandler = () => {
    const [searchParams, setSearchParams] = useSearchParams()
    const {isAuthenticated, loginUsingToken} = useAuth()
    const navigate = useNavigate()

    useEffect(() => {
        if (isAuthenticated()) {
            navigate('/dashboard')
        } else {
            const token = searchParams.get("token")
            if (token) {
                loginUsingToken(token).then(() => {
                    navigate("/dashboard")
                    successNotification(
                        'Sign in Success',
                        'You have successfully signed in'
                    )
                }).catch(err => {
                    navigate('/')
                    console.log(err)
                    errorNotification(
                        err.code,
                        err.response.data.message
                    )
                })
            } else {
                navigate('/')
                errorNotification(
                    "Login Failed",
                    searchParams.get("error") ?? "Failed to login due to unknown"
                )
            }
        }
    })

    return <p>Token processing...</p>
}

export default RedirectHandler;