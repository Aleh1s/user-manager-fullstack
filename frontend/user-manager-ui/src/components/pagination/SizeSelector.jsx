import {Flex, Select, Text} from "@chakra-ui/react";

const SizeSelector = ({size, setSize, setPage}) => {
    return (
        <Flex
            justifyContent={'space-between'}
            alignItems={'center'}
            gap={'10px'}
        >
            <Text>Size</Text>
            <Select
                w={'100px'}
                bg={'white'}
                value={size}
                onChange={(e) => {
                    setPage(0)
                    setSize(parseInt(e.target.value))
                }}
            >
                <option value='25'>25</option>
                <option value='50'>50</option>
                <option value='75'>75</option>
                <option value='100'>100</option>
            </Select>
        </Flex>
    )
}

export default SizeSelector