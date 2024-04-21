import Grid2 from "@mui/material/Unstable_Grid2";
import Button from "@mui/material/Button";
import {Apple, Computer, Microsoft} from "@mui/icons-material";

export default function DownloadBar() {
    return <Grid2
        container
        alignItems="center"
        spacing={2}
        sx={{mt: 2}}>
        <Grid2 md={4}>
            <Button color={"secondary"} variant="contained" startIcon={<Apple/>}>
                MacOS
            </Button>
        </Grid2>
        <Grid2 md={4}>
            <Button color={"secondary"} variant="contained" startIcon={<Computer/>}>
                Linux
            </Button>
        </Grid2>
        <Grid2 md={4}>
            <Button color={"secondary"} variant="contained" startIcon={<Microsoft/>}>
                Windows
            </Button>
        </Grid2>
    </Grid2>
}
