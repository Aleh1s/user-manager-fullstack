import {
    AlertDialog,
    AlertDialogBody,
    AlertDialogContent, AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogOverlay,
    Button, useDisclosure
} from "@chakra-ui/react";
import {useRef} from "react";
import {deleteCustomerById} from "../services/clients.js";
import {errorNotification, successNotification} from "../services/notification.js";

const DeleteCustomerButtonWithAlert = ({id, fetchCustomers}) => {
    const { isOpen, onOpen, onClose } = useDisclosure()
    const cancelRef = useRef()

    const deleteCustomer = () => {
        deleteCustomerById(id).then(res => {
            console.log(res)
            successNotification(
                "Customer deleted successfully",
                `Customer with id ${id} deleted successfully`
            )
            fetchCustomers()
        }).catch(err => {
            console.log(err)
            errorNotification(
                err.code,
                err.response.data.message
            )
        }).finally(() => {
            onClose()
        })
    }

    return (
        <>
            <Button colorScheme='red' onClick={onOpen} rounded={'full'}>
                Delete Customer
            </Button>

            <AlertDialog
                isOpen={isOpen}
                leastDestructiveRef={cancelRef}
                onClose={onClose}
            >
                <AlertDialogOverlay>
                    <AlertDialogContent>
                        <AlertDialogHeader fontSize='lg' fontWeight='bold'>
                            Delete Customer
                        </AlertDialogHeader>

                        <AlertDialogBody>
                            Are you sure? You can't undo this action afterwards.
                        </AlertDialogBody>

                        <AlertDialogFooter>
                            <Button ref={cancelRef} onClick={onClose}>
                                Cancel
                            </Button>
                            <Button colorScheme='red' onClick={deleteCustomer} ml={3}>
                                Delete
                            </Button>
                        </AlertDialogFooter>
                    </AlertDialogContent>
                </AlertDialogOverlay>
            </AlertDialog>
        </>
    )
}

export default DeleteCustomerButtonWithAlert;