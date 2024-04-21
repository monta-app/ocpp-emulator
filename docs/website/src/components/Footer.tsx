import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import IconButton from '@mui/material/IconButton';
import Link from '@mui/material/Link';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import LinkedInIcon from '@mui/icons-material/LinkedIn';
import {GitHub} from "@mui/icons-material";

function Copyright() {
    return <Typography variant="body2" mt={1}>
        {'Copyright © '}
        <Link href="https://monta.com/">Monta&nbsp;</Link>
        {new Date().getFullYear()}
    </Typography>
}

export default function Footer() {
    return (
        <Container
            sx={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                gap: {xs: 4, sm: 8},
                py: {xs: 8, sm: 10},
                textAlign: {sm: 'center', md: 'left'},
            }}>
            <Box
                sx={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    pt: {xs: 4, sm: 8},
                    width: '100%'
                }}
            >
                <div>
                    <Copyright/>
                </div>
                <Stack
                    direction="row"
                    justifyContent="left"
                    spacing={1}
                    useFlexGap
                    sx={{
                        color: 'text.secondary',
                    }}>
                    <IconButton
                        color="inherit"
                        href="https://github.com/monta-app"
                        aria-label="GitHub"
                        sx={{alignSelf: 'center'}}>
                        <GitHub/>
                    </IconButton>
                    <IconButton
                        color="inherit"
                        href="https://www.linkedin.com/company/montaapp/"
                        aria-label="LinkedIn"
                        sx={{alignSelf: 'center'}}>
                        <LinkedInIcon/>
                    </IconButton>
                </Stack>
            </Box>
        </Container>
    );
}
