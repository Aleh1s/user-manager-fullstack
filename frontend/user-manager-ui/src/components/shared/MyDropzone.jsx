import {useCallback} from "react";
import {useDropzone} from "react-dropzone";
import {Box} from "@chakra-ui/react";
import {uploadCustomerProfileImage} from "../../services/clients.js";
import {errorNotification, successNotification} from "../../services/notification.js";

const MyDropzone = ({id, fetchCustomers}) => {
    const onDrop = useCallback(acceptedFiles => {
        const formData = new FormData()
        formData.append('file', acceptedFiles[0])
        uploadCustomerProfileImage(
            id,
            formData
        ).then(res => {
            successNotification(
                "Success",
                "Profile image uploaded successfully"
            )
            fetchCustomers()
        }).catch(err => {
            console.log(err)
            errorNotification(
                "Error",
                "Profile image upload failed"
            )
        })
    }, [])

    const {getRootProps, getInputProps, isDragActive} = useDropzone({onDrop})

    return (
        <Box {...getRootProps()}
             w={'100%'}
             textAlign={'center'}
             border={'dashed'}
             borderColor={'gray.200'}
             borderRadius={'3xl'}
             rounded={'md'}
             p={6}
        >
            <input {...getInputProps()} />
            {
                isDragActive ?
                    <p>Drop the files here ...</p> :
                    <p>Drag 'n' drop some files here, or click to select files</p>
            }
        </Box>
    )
}

export default MyDropzone