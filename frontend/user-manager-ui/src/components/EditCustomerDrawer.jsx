import {
    Button,
    Drawer,
    DrawerBody,
    DrawerCloseButton,
    DrawerContent,
    DrawerFooter,
    DrawerHeader,
    DrawerOverlay,
    useDisclosure
} from "@chakra-ui/react";
import {useState} from "react";
import {getCustomerById} from "../services/clients.js";
import {errorNotification} from "../services/notification.js";
import EditCustomerForm from "./EditCustomerForm.jsx";

const CloseIcon = () => "x"

const EditCustomerDrawer = ({ id, fetchCustomers }) => {

    const {isOpen, onOpen, onClose} = useDisclosure()
    const [customer, setCustomer] = useState({})

    const fetchCustomerToEdit = () => {
        getCustomerById(id).then(res => {
            onOpen()
            setCustomer(res.data)
        }).catch(err => {
            console.log(err)
            errorNotification(
                err.code,
                err.response.data.message
            )
        })
    }

    return (
        <>
            <Button onClick={fetchCustomerToEdit}
                    colorScheme="teal"
                    rounded={'full'}>
                Edit
            </Button>
            <Drawer isOpen={isOpen} onClose={onClose} size={'xl'}>
                <DrawerOverlay/>
                <DrawerContent>
                    <DrawerCloseButton/>
                    <DrawerHeader>Edit Customer</DrawerHeader>

                    <DrawerBody>
                        <EditCustomerForm {...customer} fetchCustomers={fetchCustomers}/>
                    </DrawerBody>

                    <DrawerFooter>
                        <Button leftIcon={<CloseIcon/>}
                                onClick={onClose}
                                colorScheme="red">
                            Close
                        </Button>
                    </DrawerFooter>
                </DrawerContent>
            </Drawer>
        </>
    )
}

export default EditCustomerDrawer;