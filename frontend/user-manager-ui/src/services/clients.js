import axios from "axios";

export const getCustomers = async () => {
    try {
        return await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`)
    } catch (error) {
        throw error
    }
}

export const saveCustomer = async (customer) => {
    try {
        return await axios.post(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`,
            customer
        )
    } catch (error) {
        throw error
    }
}

export const deleteCustomerById = async (id) => {
    try {
        return await axios.delete(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${id}`
        )
    } catch (error) {
        throw error
    }
}

export const getCustomerById = async (id) => {
    try {
        return await axios.get(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${id}`
        )
    } catch (error) {
        throw error
    }
}

export const updateCustomerById = async (id, customer) => {
    try {
        return await axios.put(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${id}`, customer
        )
    } catch (error) {
        throw error
    }
}