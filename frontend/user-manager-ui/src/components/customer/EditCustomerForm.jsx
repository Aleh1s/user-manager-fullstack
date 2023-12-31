import {Box, Button, Image, Stack, VStack} from "@chakra-ui/react";
import {Form, Formik} from "formik";
import {getCustomerProfileImageUrl, updateCustomerById} from "../../services/clients.js";
import {errorNotification, successNotification} from "../../services/notification.js";
import MyTextInput from "../shared/MyTextInput.jsx";
import MySelect from "../shared/MySelect.jsx";
import {EditCustomerSchema} from "../validation/Schemas.jsx";
import MyDropzone from "../shared/MyDropzone.jsx";

const EditCustomerForm = ({id, name, email, age, gender, fetchCustomers}) => {
    return (
        <Box>
            <VStack
                spacing={5}
                mb={5}
            >
                <Image
                    borderRadius={'full'}
                    boxSize={'150px'}
                    objectFit={'cover'}
                    src={getCustomerProfileImageUrl(id)}
                />
                <MyDropzone id={id} fetchCustomers={fetchCustomers}/>
            </VStack>
            <Formik
                validateOnMount={true}
                initialValues={{
                    name: name,
                    email: email,
                    age: age,
                    gender: gender,
                }}
                validationSchema={EditCustomerSchema}
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

                            <Button isDisabled={!isValid || isSubmitting} type="submit">Submit</Button>
                        </Stack>
                    </Form>
                )}
            </Formik>
        </Box>
    );
}

export default EditCustomerForm;