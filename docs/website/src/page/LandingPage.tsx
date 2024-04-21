import CssBaseline from '@mui/material/CssBaseline';
import {createTheme, ThemeProvider} from '@mui/material/styles';
import Hero from '@/components/Hero';
import Footer from '@/components/Footer';

export default function LandingPage() {
    const defaultTheme = createTheme({
        palette: {
            primary: {
                main: '#FF5252',
            },
            secondary: {
                main: '#4B506B',
            }
        },
        typography: {
            fontFamily: [
                'Lato',
                'BlinkMacSystemFont',
                '"Segoe UI"',
                'Roboto',
                '"Helvetica Neue"',
                'Arial',
                'sans-serif',
                '"Apple Color Emoji"',
                '"Segoe UI Emoji"',
                '"Segoe UI Symbol"',
            ].join(','),
            body1: {
                fontSize: 20,
                fontWeight: 400,
                lineHeight: 1.5
            }
        },
        components: {
            MuiButton: {
                styleOverrides: {
                    root: {
                        background: 'linear-gradient(90deg, rgba(255,49,98,1) 0%, rgba(255,82,82,1) 100%)',
                        borderRadius: 'var(--radius-r200, 8px)',
                        textTransform: 'none',
                    }
                },
            },
        }
    });

    return (
        <ThemeProvider theme={defaultTheme}>
            <CssBaseline/>
            <Hero/>
            <Footer/>
        </ThemeProvider>
    );
}
