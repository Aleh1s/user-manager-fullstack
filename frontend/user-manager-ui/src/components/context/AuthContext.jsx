import {
    createContext,
    useContext, useEffect,
    useState
} from 'react'
import {login as performLogin} from "../../services/clients.js";
import jwtDecode from "jwt-decode";

const AuthContext = createContext({})

const AuthProvider = ({children}) => {
    const [customer, setCustomer] = useState(null)

    useEffect(() => {
        const token = localStorage.getItem("access_token");
        if (token) {
            const {sub: username, scopes: roles} = jwtDecode(token)
            setCustomer({
                username,
                roles
            })
        }
    }, [])

    const login = async (loginRequest) => {
        return new Promise((resolve, reject) => {
            performLogin(loginRequest).then(res => {
                const token = res.headers['authorization']

                localStorage.setItem("access_token", token)
                const {sub: username, scopes: roles} = jwtDecode(token)

                setCustomer({
                    username,
                    roles
                })

                resolve(res)
            }).catch(err => {
                reject(err)
            })
        })
    }

    const logout = () => {
        if (localStorage.getItem("access_token")) {
            localStorage.removeItem("access_token")
        }
        setCustomer(null)
    }

    const isAuthenticated = () => {
        const token = localStorage.getItem('access_token')
        if (!token) {
            return false
        }

        const { exp: expiration } = jwtDecode(token)

        if (Date.now() > expiration * 1000) {
            logout()
            return false
        }

        return true
    }

    return (
        <AuthContext.Provider value={{customer, login, logout, isAuthenticated}}>
            {children}
        </AuthContext.Provider>
    )
}
export const useAuth = () => useContext(AuthContext)

export default AuthProvider