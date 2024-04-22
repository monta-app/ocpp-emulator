import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import Button from "@mui/material/Button";
import {GitHub} from "@mui/icons-material";
import DownloadBar from "@/components/DownloadBar.tsx";

export default function Hero() {
    return <Box>
        <Container
            sx={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                pt: {xs: 3, sm: 3},
                pb: {xs: 8, sm: 12},
            }}>
            <Stack spacing={2} useFlexGap sx={{width: {xs: '100%', sm: '70%'}}}>
                <Box sx={{
                    display: 'flex',
                    flexDirection: {xs: 'column', md: 'row'},
                    alignSelf: 'center'
                }}>
                    <img src='/img/icon.webp' width='200'/>
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
                    Bootstrap your development workflow or explore our products using our open source OCPP Emulator; its
                    totally free and easy to use.
                </Typography>
                <Stack spacing={2} sx={{mt: 2, alignSelf: 'center'}} textAlign={'center'}>
                    <Typography variant={"h5"} sx={{fontWeight: 'bold'}}>Download</Typography>
                    <DownloadBar/>
                    <Typography variant={"h5"} sx={{fontWeight: 'bold'}}>Or</Typography>
                    <Button href="https://github.com/monta-app/ocpp-emulator"
                            variant="contained"
                            startIcon={<GitHub/>}>
                        View on Github
                    </Button>
                </Stack>
            </Stack>

            <Box sx={{
                mt: {xs: 8, sm: 4},
                alignSelf: 'center',
                height: {xs: 200, sm: 700},
                width: '100%'
            }}>
                <img src={'/img/hero.webp'}
                     width='100%'/>
            </Box>
        </Container>
    </Box>
}
