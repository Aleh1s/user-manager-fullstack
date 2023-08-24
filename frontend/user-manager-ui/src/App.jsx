import SidebarWithHeader from "./components/shared/SideBar.jsx";
import {useEffect, useState} from "react";
import {getCustomers} from "./services/clients.js";
import {Spinner, Text, Wrap, WrapItem} from "@chakra-ui/react";
import CustomerCard from "./components/customer/CustomerCard.jsx";
import CreateCustomerDrawer from "./components/customer/CreateCustomerDrawer.jsx";
import {errorNotification} from "./services/notification.js";

function App() {

    const [isLoading, setLoading] = useState(false);
    const [customers, setCustomers] = useState([]);

    const fetchCustomers = () => {
        setLoading(true)
        getCustomers().then(res => {
            setCustomers(res.data)
        }).catch(err => {
            console.log(err)
            errorNotification(
                err.code,
                err.response.data.message
            )
        }).finally(() => {
            setLoading(false)
        })
    }

    useEffect(() => {
        fetchCustomers()
    }, [])

    if (isLoading) {
        return (
            <SidebarWithHeader>
                <Spinner
                    thickness='4px'
                    speed='0.65s'
                    emptyColor='gray.200'
                    color='blue.500'
                    size='xl'
                />
            </SidebarWithHeader>
        )
    }

    return (
        <SidebarWithHeader>
            <CreateCustomerDrawer fetchCustomers={fetchCustomers}/>
            {!customers.length
                ? <Text mt={2}>No customers found</Text>
                : <Wrap justify={"center"} spacing={"30px"}>
                    {customers.map((customer, index) => (
                        <WrapItem key={index}>
                            <CustomerCard {...customer} fetchCustomers={fetchCustomers}/>
                        </WrapItem>
                    ))}
                </Wrap>
            }
        </SidebarWithHeader>
    )
}

export default App
