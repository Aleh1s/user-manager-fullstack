import {
    Heading,
    Avatar,
    Box,
    Center,
    Image,
    Flex,
    Text,
    Stack,
    useColorModeValue, Tag
} from '@chakra-ui/react';
import DeleteCustomerButtonWithAlert from "./DeteleCustomerButtonWithAlert.jsx";
import EditCustomerDrawer from "./EditCustomerDrawer.jsx";
import {getCustomerProfileImageUrl} from "../../services/clients.js";

export default function CustomerCard({id, name, email, age, gender, profileImageId, profileImageUrl, fetchCustomers}) {
    return (
        <Center py={6}>
            <Box
                w={'320px'}
                bg={useColorModeValue('white', 'gray.800')}
                boxShadow={'2xl'}
                rounded={'md'}
                overflow={'hidden'}>
                <Image
                    h={'120px'}
                    w={'full'}
                    src={
                        'https://images.unsplash.com/photo-1612865547334-09cb8cb455da?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80'
                    }
                    objectFit={'cover'}
                />
                <Flex justify={'center'} mt={-12}>
                    <Avatar
                        size={'xl'}
                        src={profileImageId ? getCustomerProfileImageUrl(id) : profileImageUrl}
                        alt={'Author'}
                        css={{
                            border: '2px solid white',
                        }}
                    />
                </Flex>

                <Box p={6}>
                    <Stack spacing={0} align={'center'} mb={5}>
                        <Tag borderRadius={"full"}>{id}</Tag>
                        <Heading fontSize={'2xl'} fontWeight={500} fontFamily={'body'}>
                            {name}
                        </Heading>
                        <Text color={'gray.500'}>{email}</Text>
                        <Text color={'gray.500'}>Age {age ?? 'None'} | {gender ? gender.substring(0, 1) + gender.substring(1, gender.length).toLowerCase() : "None"}</Text>
                    </Stack>
                </Box>
                <Flex justify={'center'} mb={3} gap={2} mx={4}>
                    <DeleteCustomerButtonWithAlert id={id} fetchCustomers={fetchCustomers}/>
                    <EditCustomerDrawer id={id} fetchCustomers={fetchCustomers}/>
                </Flex>
            </Box>
        </Center>
    );
}