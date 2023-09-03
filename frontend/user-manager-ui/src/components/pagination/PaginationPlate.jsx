import {Button} from "@chakra-ui/react";

const PaginationPlate = ({children, onClick, ...props}) => {
    return (
        <Button
            {...props}
            colorScheme={'teal'}
            w={'40px'} h={'40px'}
            onClick={onClick}
        >
            {children}
        </Button>
    )
}

export default PaginationPlate