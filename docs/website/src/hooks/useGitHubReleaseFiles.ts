import {useEffect, useState} from 'react';

const useGitHubReleaseFiles = () => {
    const [fileLinks, setFileLinks] = useState({dmg: '', exe: '', deb: ''});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchReleaseFiles = async () => {
            setLoading(true);
            try {
                // Fetch the latest release from the GitHub API
                const response = await fetch('https://api.github.com/repos/monta-app/ocpp-emulator/releases/latest');
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                const data = await response.json();

                // Initialize file links
                let dmgLink = '';
                let exeLink = '';
                let debLink = '';

                // Iterate through assets to find relevant files
                // @ts-ignore
                data.assets.forEach(asset => {
                    if (asset.name.endsWith('.dmg')) {
                        dmgLink = asset.browser_download_url;
                    } else if (asset.name.endsWith('.exe')) {
                        exeLink = asset.browser_download_url;
                    } else if (asset.name.endsWith('.deb')) {
                        debLink = asset.browser_download_url;
                    }
                });

                // Set the state with the found links
                setFileLinks({dmg: dmgLink, exe: exeLink, deb: debLink});
                setLoading(false);
            } catch (err) {
                // @ts-ignore
                setError(err.message);
                setLoading(false);
            }
        };

        void fetchReleaseFiles();
    }, []);

    return {fileLinks, loading, error};
};

export default useGitHubReleaseFiles;
