import {Formik, Form} from 'formik';
import * as Yup from 'yup';
import {Box, Button, Stack} from "@chakra-ui/react";
import {saveCustomer} from "../../services/clients.js";
import {errorNotification, successNotification} from "../../services/notification.js";
import MyTextInput from "../shared/MyTextInput.jsx";
import MySelect from "../shared/MySelect.jsx";
import {RegistrationSchema} from "../validation/Schemas.jsx";

const CreateCustomerForm = ({ fetchCustomers }) => {
    return (
        <Box>
            <Formik
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
                    saveCustomer(customer).then(() => {
                        successNotification(
                            "Customer created successfully",
                            `${customer.name} was created successfully`
                        )
                        fetchCustomers()
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
                        <Stack spacing={'24px'}>
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

                            <Button isDisabled={!isValid || isSubmitting} type="submit">Submit</Button>
                        </Stack>
                    </Form>
                )}
            </Formik>
        </Box>
    );
};

export default CreateCustomerForm