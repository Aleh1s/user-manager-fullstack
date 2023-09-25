import {Center, HStack, Link} from '@chakra-ui/react'
import {GitHubIcon, GoogleIcon} from './IconProvider.jsx'
import {GITHUB_AUTH_URL, GOOGLE_AUTH_URL} from "../constant/constants.js";

const providers = [
    {name: 'Google', icon: <GoogleIcon/>, link: GOOGLE_AUTH_URL},
    {name: 'GitHub', icon: <GitHubIcon/>, link: GITHUB_AUTH_URL},
]

const OAuthButtonGroup = () => {
    return (
        <HStack spacing="4">
            {providers.map(({name, icon, link}) => (
                <Link key={name} href={link} w={'full'} h={'40px'} border={'1px'} borderRadius={'5px'}
                      borderColor={'rgb(226, 232, 240)'} _hover={{bg: 'rgb(241,243,246)'}}>
                    <Center h={'full'}>
                        {icon}
                    </Center>
                </Link>
            ))}
        </HStack>
    )
}

export default OAuthButtonGroup