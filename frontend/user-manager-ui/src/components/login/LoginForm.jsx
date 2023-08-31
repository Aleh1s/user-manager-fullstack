import {Form, Formik} from "formik";
import * as Yup from "yup";
import {Button, Stack} from "@chakra-ui/react";
import MyTextInput from "../shared/MyTextInput.jsx";
import {errorNotification} from "../../services/notification.js";
import {useAuth} from "../context/AuthContext.jsx";
import {useNavigate} from "react-router-dom";

const LoginForm = () => {

    const { login } = useAuth()
    const navigate = useNavigate()

    return (
        <Formik
            validateOnMount={true}
            initialValues={{username: '', password: ''}}
            validationSchema={Yup.object({
                username: Yup.string()
                    .email("Must be valid email")
                    .required("Email is required"),
                password: Yup.string()
                    .min(6, "Password must be at least 6 characters")
                    .max(20, "Password must be 20 characters or less")
                    .required("Password is required")
            })}
            onSubmit={(values, {setSubmitting}) => {
                setSubmitting(true)
                login(values).then(res => {
                    navigate('/dashboard')
                }).catch(err => {
                    errorNotification(
                        err.code,
                        err.response.data.message
                    )
                }).finally(() => {
                    setSubmitting(false)
                })
            }}>

            {({isValid, isSubmitting}) => {
                return (
                    <Form>
                        <Stack spacing={4}>
                            <MyTextInput
                                label={'Email'}
                                name={'username'}
                                type={'email'}
                                placeholder={'example@example.com'}
                            />
                            <MyTextInput
                                label={'Password'}
                                name={'password'}
                                type={'password'}
                                placeholder={'********'}
                            />
                            <Button
                                type={'submit'}
                                isDisabled={!isValid || isSubmitting}
                            >
                                Login
                            </Button>
                        </Stack>
                    </Form>
                )
            }}
        </Formik>
    )
}

export default LoginForm