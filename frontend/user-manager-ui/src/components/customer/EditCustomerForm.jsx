import {Box, Button, Stack} from "@chakra-ui/react";
import {Form, Formik} from "formik";
import * as Yup from "yup";
import {updateCustomerById} from "../../services/clients.js";
import {errorNotification, successNotification} from "../../services/notification.js";
import MyTextInput from "../shared/MyTextInput.jsx";
import MySelect from "../shared/MySelect.jsx";

const EditCustomerForm = ({id, name, email, age, gender, fetchCustomers}) => {
    return (
        <Box>
            <Formik
                initialValues={{
                    name: name,
                    email: email,
                    age: age,
                    gender: gender,
                }}
                validationSchema={Yup.object({
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
                })}
                onSubmit={(customer, {setSubmitting}) => {
                    setSubmitting(true)
                    updateCustomerById(id, customer).then(() => {
                        successNotification(
                            "Customer updated successfully",
                            `${customer.name} was updated successfully`
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

                            <Button disabled={!isValid || isSubmitting} type="submit">Submit</Button>
                        </Stack>
                    </Form>
                )}
            </Formik>
        </Box>
    );
}

export default EditCustomerForm;