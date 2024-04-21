import Grid2 from "@mui/material/Unstable_Grid2";
import Button from "@mui/material/Button";
import {Apple, Computer, Microsoft} from "@mui/icons-material";
import useGitHubReleaseFiles from "@/hooks/useGitHubReleaseFiles.ts";

export default function DownloadBar() {

    const {fileLinks} = useGitHubReleaseFiles();

    return <Grid2
        container
        alignItems="center"
        spacing={2}
        sx={{mt: 2}}>
        <Grid2 xs={12} md={4}>
            <Button
                fullWidth
                href={fileLinks.dmg}
                variant="contained"
                startIcon={<Apple/>}>
                MacOS
            </Button>
        </Grid2>
        <Grid2 xs={12} md={4}>
            <Button
                fullWidth
                href={fileLinks.deb}
                variant="contained"
                startIcon={<Computer/>}>
                Linux
            </Button>
        </Grid2>
        <Grid2 xs={12} md={4}>
            <Button
                fullWidth
                href={fileLinks.exe}
                variant="contained"
                startIcon={<Microsoft/>}>
                Windows
            </Button>
        </Grid2>
    </Grid2>
}
