import SidebarWithHeader from "./components/shared/SideBar.jsx";
import {useEffect, useState} from "react";
import {getCustomers} from "./services/clients.js";
import {Center, Flex, HStack, Select, Spinner, Text, Wrap, WrapItem} from "@chakra-ui/react";
import CustomerCard from "./components/customer/CustomerCard.jsx";
import CreateCustomerDrawer from "./components/customer/CreateCustomerDrawer.jsx";
import {errorNotification} from "./services/notification.js";
import PaginationPanel from "./components/pagination/PaginationPanel.jsx";
import SizeSelector from "./components/pagination/SizeSelector.jsx";

function App() {

    const [isLoading, setLoading] = useState(false);
    const [customers, setCustomers] = useState([]);

    const [page, setPage] = useState(0);
    const [size, setSize] = useState(25);
    const [totalPages, setTotalPages] = useState(0);

    const fetchCustomers = () => {
        setLoading(true)
        getCustomers(page, size).then(res => {
            const {content: customers, totalPages} = res.data;
            setCustomers(customers)
            setTotalPages(totalPages)
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
    }, [page, size])

    if (isLoading) {
        return (
            <SidebarWithHeader>
                <Center style={{height: '100vh'}}>
                    <Spinner
                        thickness='4px'
                        speed='0.65s'
                        emptyColor='gray.200'
                        color='blue.500'
                        size='xl'
                    />
                </Center>
            </SidebarWithHeader>
        )
    }

    return (
        <SidebarWithHeader>
            <Flex justifyContent={'space-between'}>
                <CreateCustomerDrawer fetchCustomers={fetchCustomers}/>
                <SizeSelector
                    size={size}
                    setSize={setSize}
                    setPage={setPage}
                />
            </Flex>
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
            <PaginationPanel
                page={page}
                setPage={setPage}
                totalPages={totalPages}
                fetchData={fetchCustomers}
            />
        </SidebarWithHeader>
    )
}

export default App
