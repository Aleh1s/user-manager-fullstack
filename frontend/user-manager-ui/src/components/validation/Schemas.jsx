import * as Yup from "yup";

const customer = {
    name: Yup.string()
        .max(50, 'Must be 50 characters or less')
        .required('Name is required'),
    email: Yup.string()
        .email('Invalid email address')
        .required('Email is required'),
    age: Yup.number()
        .min(16, 'Must be at least 16')
        .max(120, 'Must be 120 or less')
        .required("Age is required"),
    gender: Yup.string()
        .oneOf(
            ['MALE', 'FEMALE'],
            'Invalid Gender Type'
        )
        .required('Gender is required'),
    password: Yup.string()
        .min(6, 'Must be at least 6 characters')
        .max(20, 'Must be 20 characters or less')
        .required('Password is required')
}


export const EditCustomerSchema = Yup.object({
    name: customer.name,
    email: customer.email,
    age: customer.age,
    gender: customer.gender
})
export const RegistrationSchema = Yup.object(customer)
export const LoginSchema = Yup.object({
    username: customer.email,
    password: customer.password
})
