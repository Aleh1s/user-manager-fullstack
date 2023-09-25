import axios from "axios";
import {API_BASE_URL} from "../components/constant/constants.js";

const getAuthConfig = () => ({
    headers: {
        Authorization: `Bearer ${localStorage.getItem("access_token")}`,
    }
})

export const getCustomers = async (page, size) => {
    try {
        return await axios.get(
            `${API_BASE_URL}/api/v1/customers?page=${page}&size=${size}`,
            getAuthConfig()
        )
    } catch (error) {
        throw error
    }
}

export const saveCustomer = async (customer) => {
    try {
        return await axios.post(
            `${API_BASE_URL}/api/v1/customers`,
            customer
        )
    } catch (error) {
        throw error
    }
}

export const deleteCustomerById = async (id) => {
    try {
        return await axios.delete(
            `${API_BASE_URL}/api/v1/customers/${id}`,
            getAuthConfig()
        )
    } catch (error) {
        throw error
    }
}

export const getCustomerById = async (id) => {
    try {
        return await axios.get(
            `${API_BASE_URL}/api/v1/customers/${id}`,
            getAuthConfig()
        )
    } catch (error) {
        throw error
    }
}

export const updateCustomerById = async (id, customer) => {
    try {
        return await axios.put(
            `${API_BASE_URL}/api/v1/customers/${id}`,
            customer,
            getAuthConfig()
        )
    } catch (error) {
        throw error
    }
}

export const login = async (loginRequest) => {
    try {
        return await axios.post(
            `${API_BASE_URL}/api/v1/auth/login`,
            loginRequest
        )
    } catch (error) {
        throw error
    }
}

export const uploadCustomerProfileImage = async (id, formData) => {
    try {
        return await axios.post(
            `${API_BASE_URL}/api/v1/customers/${id}/profile-image`,
            formData,
            {
                ...getAuthConfig(),
                'Content-Type': 'multipart/form-data'
            }
        )
    } catch (error) {
        throw error
    }
}

export const getCustomerProfileImageUrl = (id) =>
    `${API_BASE_URL}/api/v1/customers/${id}/profile-image`