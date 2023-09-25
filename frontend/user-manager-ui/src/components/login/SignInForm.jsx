import {Form, Formik} from "formik";
import {Button, Link, Stack} from "@chakra-ui/react";
import MyTextInput from "../shared/MyTextInput.jsx";
import {errorNotification} from "../../services/notification.js";
import {useAuth} from "../context/AuthContext.jsx";
import {useNavigate} from "react-router-dom";
import {LoginSchema} from "../validation/Schemas.jsx";
import OAuth2ButtonGroup from "../oauth2/OAuth2ButtonGroup.jsx";

const SignInForm = () => {

    const { login } = useAuth()
    const navigate = useNavigate()

    return (
        <Formik
            validateOnMount={true}
            initialValues={{username: '', password: ''}}
            validationSchema={LoginSchema}
            onSubmit={(values, {setSubmitting}) => {
                setSubmitting(true)
                login(values).then(() => {
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
                                Sign in
                            </Button>
                            <Link href={'/registration'} color={'blue.500'}>
                                Have no account?
                            </Link>
                            <OAuth2ButtonGroup/>
                        </Stack>
                    </Form>
                )
            }}
        </Formik>
    )
}

export default SignInForm