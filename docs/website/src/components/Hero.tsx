import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import DownloadBar from "../components/DownloadBar.tsx";
import MontaLogo from "../components/MontaLogo.tsx";

export default function Hero() {
    return <Box>
        <Container
            sx={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                pt: {xs: 7, sm: 7},
                pb: {xs: 8, sm: 12},
            }}>
            <Stack spacing={2} useFlexGap sx={{width: {xs: '100%', sm: '70%'}}}>
                <Box sx={{
                    display: 'flex',
                    flexDirection: {xs: 'column', md: 'row'},
                    alignSelf: 'center'
                }}>
                    <MontaLogo/>
                </Box>
                <Typography
                    variant="h4"
                    sx={{
                        mt: 4,
                        display: 'flex',
                        flexDirection: {xs: 'column', md: 'row'},
                        alignSelf: 'center',
                        textAlign: 'center',

                    }}>
                    OCPP Emulator
                </Typography>
                <Typography
                    textAlign="center"
                    sx={{alignSelf: 'center', width: {sm: '100%', md: '80%'}}}>
                    Bootstrap your development workflow or explore our products using our open source OCPP Emulator,
                    it's totally free and easy to use.
                </Typography>
                <Stack
                    direction={{xs: 'column', sm: 'row'}}
                    alignSelf="center"
                    spacing={1}
                    useFlexGap
                    sx={{pt: 2, width: {xs: '100%', sm: 'auto'}}}>
                    <DownloadBar/>
                </Stack>
            </Stack>

            <Box sx={{
                mt: {xs: 8, sm: 10},
                alignSelf: 'center',
                height: {xs: 200, sm: 700},
                width: '100%'
            }}>
                <img src={'/hero.png'}
                     width='100%'/>
            </Box>
        </Container>
    </Box>
}
