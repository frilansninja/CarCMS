import { useEffect, useState } from "react";
import { Container, Typography, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Button } from "@mui/material";

const ServiceIntervalsPage = () => {
  const [serviceIntervals, setServiceIntervals] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/api/service-intervals/grouped")
        .then(response => response.json())
        .then(data => setServiceIntervals(data))
        .catch(error => console.error("Fel vid hämtning:", error));
  }, []);

  return (
      <Container>
        <Typography variant="h4" gutterBottom>
          Serviceintervall
        </Typography>
        <Button variant="contained" color="primary" style={{ marginBottom: "20px" }}>
          Lägg till nytt serviceintervall
        </Button>
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Märke</TableCell>
                <TableCell>Modell</TableCell>
                <TableCell>Årsmodell Start</TableCell>
                <TableCell>Årsmodell Slut</TableCell>
                <TableCell>Serviceåtgärder</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {serviceIntervals.map((interval, index) => (
                  <TableRow key={index}>
                    <TableCell>{interval.make}</TableCell>
                    <TableCell>{interval.model}</TableCell>
                    <TableCell>{interval.modelYearStart}</TableCell>
                    <TableCell>{interval.modelYearEnd}</TableCell>
                    <TableCell>{interval.serviceTypes.join(", ")}</TableCell>
                  </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Container>
  );
};

export default ServiceIntervalsPage;
