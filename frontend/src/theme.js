import { createTheme } from '@mui/material/styles';

// Skapa ett konsekvent tema för hela applikationen
const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2', // Blå
      light: '#42a5f5',
      dark: '#1565c0',
      contrastText: '#fff',
    },
    secondary: {
      main: '#dc004e', // Rosa/röd
      light: '#ff5983',
      dark: '#9a0036',
      contrastText: '#fff',
    },
    success: {
      main: '#2e7d32', // Grön
      light: '#4caf50',
      dark: '#1b5e20',
    },
    warning: {
      main: '#ed6c02', // Orange
      light: '#ff9800',
      dark: '#e65100',
    },
    error: {
      main: '#d32f2f', // Röd
      light: '#ef5350',
      dark: '#c62828',
    },
    info: {
      main: '#0288d1', // Ljusblå
      light: '#03a9f4',
      dark: '#01579b',
    },
    background: {
      default: '#f5f5f5',
      paper: '#ffffff',
    },
  },
  typography: {
    fontFamily: [
      '-apple-system',
      'BlinkMacSystemFont',
      '"Segoe UI"',
      'Roboto',
      '"Helvetica Neue"',
      'Arial',
      'sans-serif',
    ].join(','),
    h4: {
      fontWeight: 600,
    },
    h5: {
      fontWeight: 600,
    },
    h6: {
      fontWeight: 600,
    },
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none', // Ingen automatisk versalisering
          borderRadius: 4,
        },
      },
    },
    MuiTextField: {
      defaultProps: {
        variant: 'outlined',
        size: 'small',
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          borderRadius: 8,
        },
      },
    },
    MuiTableCell: {
      styleOverrides: {
        head: {
          fontWeight: 600,
          backgroundColor: '#f5f5f5',
        },
      },
    },
  },
  spacing: 8, // Standard spacing unit (8px)
});

export default theme;
