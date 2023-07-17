import SidebarWithHeader from "./components/shared/SideBar.jsx";
import {useEffect, useState} from "react";
import {getCustomers} from "./services/clients.js";
import {Alert, AlertIcon, Spinner, Text, Wrap, WrapItem} from "@chakra-ui/react";
import CardWithImage from "./components/CardWithImage.jsx";

function App() {

    const [isLoading, setLoading] = useState(false);
    const [customers, setCustomers] = useState([]);
    const [isError, setError] = useState(false)

    useEffect(() => {
        setLoading(true)
        getCustomers().then(res => {
            setCustomers(res.data)
        }).catch(err => {
            console.log(err)
            setError(true)
        }).finally(() => {
            setLoading(false)
        })
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

    if (isError) {
        return (
            <SidebarWithHeader>
                <Alert status='error'>
                    <AlertIcon/>
                    There was an error processing your request
                </Alert>
            </SidebarWithHeader>
        )
    }

    if (!customers.length) {
        return (
            <SidebarWithHeader>
                <Text>No customers found</Text>
            </SidebarWithHeader>
        )
    }

    return (
        <SidebarWithHeader>
            <Wrap justify={"center"} spacing={"30px"}>
                {customers.map((customer, index) => (
                    <WrapItem key={index}>
                        <CardWithImage {...customer}/>
                    </WrapItem>
                ))}
            </Wrap>
        </SidebarWithHeader>
    )
}

export default App
