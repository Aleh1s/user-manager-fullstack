import {Box, Button, HStack, Link, Stack, useColorModeValue} from "@chakra-ui/react";
import {Form, Formik} from "formik";
import {saveCustomer} from "../../services/clients.js";
import {errorNotification, successNotification} from "../../services/notification.js";
import MyTextInput from "../shared/MyTextInput.jsx";
import MySelect from "../shared/MySelect.jsx";
import {RegistrationSchema} from "../validation/Schemas.jsx";
import {useAuth} from "../context/AuthContext.jsx";
import {useNavigate} from "react-router-dom";
import * as Yup from "yup";

const SignUpForm = () => {

    const {loginUsingToken} = useAuth();
    const navigate = useNavigate()

    return (
        <Box
            rounded={'lg'}
            bg={useColorModeValue('white', 'gray.700')}
            boxShadow={'lg'}
            p={8}
        >
            <Formik
                validateOnMount={true}
                initialValues={{
                    name: '',
                    email: '',
                    age: 0,
                    password: '',
                    gender: '',
                }}
                validationSchema={RegistrationSchema}
                onSubmit={(customer, {setSubmitting}) => {
                    setSubmitting(true)
                    saveCustomer(customer).then((res) => {
                        const token = res.headers['authorization']
                        loginUsingToken(token).then(() => {
                            navigate("/dashboard")
                            successNotification(
                                "SignUp Success",
                                `${customer.name} was registered`
                            )
                        })
                    }).catch(err => {
                        console.log(err)
                        errorNotification(
                            err.code,
                            err.response.data.message
                        )
                    }).finally(() => {
                        setSubmitting(false)
                    })
                }}
            >
                {({isValid, isSubmitting}) => (
                    <Form>
                        <Stack spacing={4}>
                            <HStack>
                                <MyTextInput
                                    label="Name"
                                    name="name"
                                    type="text"
                                    placeholder="Jane"
                                />

                                <MyTextInput
                                    label="Email"
                                    name="email"
                                    type="email"
                                    placeholder="jane@formik.com"
                                />
                            </HStack>

                            <MyTextInput
                                label="Password"
                                name="password"
                                type="password"
                                placeholder="********"
                            />

                            <MyTextInput
                                label="Age"
                                name="age"
                                type="number"
                                placeholder="18"
                            />
                            <MySelect label="Gender" name="gender">
                                <option value="">Select a gender</option>
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                            </MySelect>

                            <Button
                                type={"submit"}
                                isDisabled={!isValid || isSubmitting}
                            >
                                Sign up
                            </Button>
                            <Link href={'/'} color={'blue.500'}>
                                Have an account?
                            </Link>
                        </Stack>
                    </Form>
                )}
            </Formik>
        </Box>
    )
}

export default SignUpForm