import PaginationPlate from "./PaginationPlate.jsx";
import {HStack} from "@chakra-ui/react";
import {ArrowLeftIcon, ArrowRightIcon} from "@chakra-ui/icons";

const PaginationPanel = ({page, setPage, totalPages, fetchData}) => {

    if (totalPages === 0) {
        return <></>
    }

    const getPlates = (startIndex, endIndex) => {
        const plates = []
        for (let i = startIndex; i < endIndex; i++) {
            plates.push(
                <PaginationPlate
                    id={`${i}`}
                    isDisabled={page === i}
                    onClick={() => setPage(i)}
                >
                    {i + 1}
                </PaginationPlate>
            )
        }
        return plates
    }

    const plates = []
    plates.push(
        <PaginationPlate
            id={'left'}
            hidden={page === 0}
            onClick={() => setPage(0)}
        >
            {<ArrowLeftIcon/>}
        </PaginationPlate>
    )
    if (totalPages > 10) {
        if (page < 5) {
            plates.push(getPlates(0, 10))
        } else if (page > totalPages - 5) {
            plates.push(getPlates(totalPages - 10, totalPages))
        } else {
            plates.push(getPlates(page - 5, page + 5))
        }
    } else {
        plates.push(getPlates(0, totalPages))
    }
    plates.push(
        <PaginationPlate
            id={'right'}
            hidden={page === totalPages - 1}
            onClick={() => setPage(totalPages - 1)}
        >
            {<ArrowRightIcon/>}
        </PaginationPlate>
    )

    return (
        <HStack justifyContent={'center'}>
            {plates}
        </HStack>
    )
}

export default PaginationPanel