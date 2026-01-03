import { useState, useEffect } from "react";
import { Calendar, Views, momentLocalizer } from "react-big-calendar";
import moment from "moment";
import "moment/locale/sv";
import "react-big-calendar/lib/css/react-big-calendar.css";
import { Button, Dialog, DialogContent, DialogTitle, TextField, MenuItem } from "@mui/material";
import { useDrag, useDrop } from "react-dnd";
import { useNavigate } from "react-router-dom";

moment.locale("sv");
moment.updateLocale("sv", {
    longDateFormat: {
        LT: "HH:mm",
        LTS: "HH:mm:ss",
        L: "YYYY-MM-DD",
        LL: "D MMMM YYYY",
        LLL: "D MMMM YYYY HH:mm",
        LLLL: "dddd D MMMM YYYY HH:mm"
    },
    months: [
        "januari", "februari", "mars", "april", "maj", "juni",
        "juli", "augusti", "september", "oktober", "november", "december"
    ],
    monthsShort: [
        "jan", "feb", "mar", "apr", "maj", "jun",
        "jul", "aug", "sep", "okt", "nov", "dec"
    ],
    weekdays: [
        "söndag", "måndag", "tisdag", "onsdag", "torsdag", "fredag", "lördag"
    ],
    weekdaysShort: [
        "sön", "mån", "tis", "ons", "tor", "fre", "lör"
    ],
    weekdaysMin: [
        "sö", "må", "ti", "on", "to", "fr", "lö"
    ],
    week: { dow: 1 }
});


const localizer = momentLocalizer(moment);

const eventStyleGetter = (event) => {
    return {
        style: {
            backgroundColor: event.categoryColor || "#2196F3",
            color: "#fff",
            borderRadius: "5px",
            padding: "5px",
            fontWeight: "bold",
            textAlign: "center"
        }
    };
};


const messages = {
    allDay: "Hela dagen",
    previous: "Föregående",
    next: "Nästa",
    today: "Idag",
    month: "Månad",
    week: "Vecka",
    day: "Dag",
    agenda: "Agenda",
    date: "Datum",
    time: "Tid",
    event: "Händelse",
    showMore: (total) => `+ Visa fler (${total})`,
    work_week: "Arbetsvecka",
    firstDay: 1
};

const formats = {
    timeGutterFormat: "HH:mm", // Visar 24h på tidsaxeln
    eventTimeRangeFormat: ({ start, end }) =>
        `${moment(start).format("HH:mm")} - ${moment(end).format("HH:mm")}`,
    agendaTimeRangeFormat: ({ start, end }) =>
        `${moment(start).format("HH:mm")} - ${moment(end).format("HH:mm")}`,
    dayFormat: "dddd D MMMM",
    weekdayFormat: (date) => moment(date).format("dddd"),
    monthHeaderFormat: "MMMM YYYY",
    dayHeaderFormat: "dddd D MMMM",
    dayRangeHeaderFormat: ({ start, end }) =>
        `${moment(start).format("D MMMM")} – ${moment(end).format("D MMMM")}`,
    agendaHeaderFormat: ({ start, end }) =>
        `${moment(start).format("D MMMM")} – ${moment(end).format("D MMMM")}`
};







const WORKDAY_START = 8;  // Arbetsdagen startar kl. 08:00
const WORKDAY_END = 17;   // Arbetsdagen slutar kl. 17:00
const BUFFER_HOURS = 1;   // Extra tid före och efter arbetstid

const CalendarView = ({ unscheduledOrders, onBookingUpdate }) => {

    const [bookings, setBookings] = useState([]);
    const [currentDate, setCurrentDate] = useState(new Date());
    const [selectedEvent, setSelectedEvent] = useState(null);
    const [openDialog, setOpenDialog] = useState(false);
    const [newOrderDialog, setNewOrderDialog] = useState(false);
    const [newOrderTitle, setNewOrderTitle] = useState("");
    const [warningDialog, setWarningDialog] = useState(false);
    const [newOrderMechanic, setNewOrderMechanic] = useState("");
    const [newOrderStart, setNewOrderStart] = useState(null);
    const [newOrderEnd, setNewOrderEnd] = useState(null);
    const [draggedTask, setDraggedTask] = useState(null);
    const [mechanics, setMechanics] = useState([]);
    const navigate = useNavigate();
    const [view, setView] = useState(Views.WEEK); // Standardvy: vecka

    const handleViewChange = (newView) => {
        console.log("Byter vy till:", newView);
        setView(newView);
    };

    useEffect(() => {
        const fetchMechanics = async () => {
            try {
                const token = localStorage.getItem("accessToken");
                const response = await fetch("/api/users/mechanics", {
                    headers: { Authorization: `Bearer ${token}` },
                });

                if (!response.ok) {
                    console.error("Fel vid hämtning av mekaniker:", response.status, response.statusText);
                    setMechanics([]);
                    return;
                }

                const data = await response.json();
                setMechanics(Array.isArray(data) ? data : []);
            } catch (error) {
                console.error("Fel vid hämtning av mekaniker:", error);
                setMechanics([]);
            }
        };

        fetchMechanics();
    }, []);


    const handleNavigate = (newDate, view) => {
        console.log("Navigating to:", newDate, "View:", view);
        setCurrentDate(newDate);
    };

    useEffect(() => {
        const fetchBookings = async () => {
            try {
                const token = localStorage.getItem("accessToken");
                const response = await fetch("/api/bookings", {
                    headers: { Authorization: `Bearer ${token}` },
                });

                if (!response.ok) {
                    console.error("Fel vid hämtning av bokningar:", response.status, response.statusText);
                    setBookings([]);
                    return;
                }

                const data = await response.json();
                console.log("📅 Data från backend:", data);

                if (!Array.isArray(data)) {
                    console.error("Bokningsdata är inte en array:", data);
                    setBookings([]);
                    return;
                }

                const formattedBookings = data.map(booking => ({
                    ...booking,
                    startTime: booking.startTime ? new Date(booking.startTime) : null,
                    endTime: booking.endTime ? new Date(booking.endTime) : null
                }));

                setBookings(formattedBookings);
            } catch (error) {
                console.error("Fel vid hämtning av bokningar:", error);
                setBookings([]);
            }
        };

        fetchBookings();
    }, []);

    useEffect(() => {
        const token = localStorage.getItem("accessToken");
        if (!token) {
            navigate("/login");
        }
    }, [navigate]);

    const handleSelectEvent = (event) => {
        setSelectedEvent(event);
        setOpenDialog(true);
    };

    const handleCloseDialog = () => {
        setOpenDialog(false);
        setSelectedEvent(null);
    };

    /*const handleDeleteOrder = () => {
        if (!selectedEvent) return;

        // Uppdatera state om bookings hanteras lokalt
        setBookings((prevBookings) => prevBookings.filter(b => b.id !== selectedEvent.id));

        // Om bookings hanteras via en extern funktion
        if (onBookingUpdate) {
            onBookingUpdate({ type: "delete", id: selectedEvent.id });
        }

        setOpenDialog(false);
        setSelectedEvent(null);
    };*/
    const handleDeleteOrder = async () => {
        if (!selectedEvent) return;

        const token = localStorage.getItem("accessToken");

        try {
            const response = await fetch(`/api/bookings/${selectedEvent.id}`, {
                method: "DELETE",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json"
                }
            });

            if (response.ok) {
                setBookings((prevBookings) => prevBookings.filter(b => b.id !== selectedEvent.id));
                setOpenDialog(false);
                setSelectedEvent(null);
            } else {
                console.error("Fel vid borttagning av bokning.");
            }
        } catch (error) {
            console.error("Nätverksfel vid borttagning av bokning:", error);
        }
    };




    const handleCloseWarning = (confirm) => {
        setWarningDialog(false);
        if (confirm && draggedTask) {
            onBookingUpdate(draggedTask);
        }
        setDraggedTask(null);
    };


    const handleSelectSlot = (slotInfo) => {
        setNewOrderStart(slotInfo.start);
        setNewOrderEnd(slotInfo.end);
        setNewOrderDialog(true);
    };

    /*const handleCreateOrder = () => {
        if (!newOrderTitle || !newOrderMechanic) return;

        const newOrder = {
            id: (bookings?.length || 0) + 1,
            title: newOrderTitle,
            mechanicIcon: "🔧",
            mechanicName: newOrderMechanic,
            categoryColor: "#4287f5",
            startTime: newOrderStart,
            endTime: newOrderEnd
        };

        setBookings((prevBookings) => [...prevBookings, newOrder]);
        setNewOrderDialog(false);
        setNewOrderTitle("");
        setNewOrderMechanic("");
    };*/

    const handleCreateOrder = async () => {
        if (!newOrderTitle || !newOrderMechanic) return;

        const newOrder = {
            title: newOrderTitle,
            mechanicId: newOrderMechanic,
            categoryColor: "#4287f5",
            startTime: newOrderStart,
            endTime: newOrderEnd
        };
        console.log("🛠️ Skickar ny order:", newOrder);

        const token = localStorage.getItem("accessToken"); // 🔹 Hämta JWT-token

        try {
            const response = await fetch("/api/bookings", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json", // 🔹 Korrigerad Content-Type
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(newOrder),
            });

            if (response.ok) {
                const savedOrder = await response.json();
                setBookings((prevBookings) => [...prevBookings, {
                    ...savedOrder,
                    startTime: new Date(savedOrder.startTime),
                    endTime: new Date(savedOrder.endTime)
                }]);
                setNewOrderDialog(false);
                setNewOrderTitle("");
                setNewOrderMechanic("");
            } else {
                console.error("Fel vid skapande av arbetsorder.");
            }
        } catch (error) {
            console.error("Nätverksfel vid skapande av arbetsorder:", error);
        }
    };



    const eventStyleGetter = (event) => {
        const backgroundColor = event.categoryColor || "#2196F3";
        if (!(event.startTime instanceof Date) || !(event.endTime instanceof Date)) {
            console.error("🚨 Fel format på startTime eller endTime!", event);
        }
        return { style: { backgroundColor, color: "#fff", borderRadius: "5px", padding: "5px" } };
    };

    const [{ isOver }, drop] = useDrop(() => ({
        accept: "TASK",
        drop: (item, monitor) => {
            const dropTime = monitor.getDropResult()?.date;

            if (dropTime) {
                // 🔹 Konvertera tiden till 24h-format med tvingad svensk lokal
                const formattedDropTime = moment(dropTime)
                    .locale("sv")
                    .format("YYYY-MM-DD HH:mm"); // 24h-format

                // 🔹 Se till att start- och sluttid explicit använder 24h-format
                const newBooking = {
                    ...item,
                    startTime: moment(formattedDropTime, "YYYY-MM-DD HH:mm")
                        .toDate(),
                    endTime: moment(formattedDropTime, "YYYY-MM-DD HH:mm")
                        .add(item.estimatedTime, "minutes")
                        .toDate()
                };

                console.log("Tvingad starttid:", newBooking.startTime);  // ✅ Debugging
                console.log("Tvingad sluttid:", newBooking.endTime);      // ✅ Debugging

                const isConflict = bookings.some((b) =>
                    moment(newBooking.startTime).isBefore(b.endTime) &&
                    moment(newBooking.endTime).isAfter(b.startTime)
                );

                if (isConflict) {
                    setDraggedTask(newBooking);
                    setWarningDialog(true);
                } else {
                    onBookingUpdate(newBooking);
                }
            }
        },
        collect: (monitor) => ({
            isOver: !!monitor.isOver()
        })
    }));



    const TaskItem = ({ task }) => {
        const [{ isDragging }, drag] = useDrag(() => ({
            type: "TASK",
            item: task,
            collect: (monitor) => ({
                isDragging: !!monitor.isDragging()
            })
        }));
        return (
            <div ref={drag} style={{ opacity: isDragging ? 0.5 : 1, padding: "5px", background: "#f1f1f1", marginBottom: "5px", cursor: "grab" }}>
                {task.mechanicIcon} {task.title} ({moment(task.creationDate).format("YYYY-MM-DD")})
            </div>
        );
    };

    return (
        <div style={{ display: "flex", height: "80vh", padding: "20px" }}>
            <div style={{ width: "250px", overflowY: "auto", padding: "10px", background: "#ececec", marginRight: "10px" }}>
                <h4>Ej schemalagda arbetsordrar</h4>
                {(unscheduledOrders || []).sort((a, b) => new Date(a.creationDate) - new Date(b.creationDate)).map(task => (
                    <TaskItem key={task.id} task={task} />
                ))}
            </div>
            <div ref={drop} style={{ flex: 1, height: "75vh", border: isOver ? "2px dashed #2196F3" : "none" }}>
            <Calendar
                    localizer={localizer}
                    events={bookings}
                    startAccessor="startTime"
                    endAccessor="endTime"
                    titleAccessor={(event) =>
                        `${event.mechanicName ? event.mechanicName : "Ingen mekaniker"} ${event.title} (${moment(event.startTime).format("HH:mm")}-${moment(event.endTime).format("HH:mm")})`
                    }
                    views={[Views.DAY, Views.WEEK,  Views.MONTH]}
                    view={view}
                    onView={handleViewChange}
                    defaultView={Views.WEEK}
                    date={currentDate}
                    selectable
                    messages={messages}
                    onNavigate={handleNavigate}
                    onSelectEvent={handleSelectEvent}
                    onSelectSlot={handleSelectSlot}
                    eventPropGetter={eventStyleGetter}
                    culture="sv"
                    formats={formats}
                    firstDayOfWeek={1}
                    min={new Date().setHours(WORKDAY_START - BUFFER_HOURS, 0, 0)} // Starttid
                    max={new Date().setHours(WORKDAY_END + BUFFER_HOURS, 0, 0)}   // Sluttid
                />

            </div>

            <Dialog open={openDialog} onClose={handleCloseDialog}>
                <DialogTitle>Redigera Bokning</DialogTitle>
                <DialogContent>
                    <p>Mekaniker: {selectedEvent?.mechanicName}</p>
                    <p>Starttid: {moment(selectedEvent?.startTime).format("YYYY-MM-DD HH:mm")}</p>
                    <p>Sluttid: {moment(selectedEvent?.endTime).format("YYYY-MM-DD HH:mm")}</p>
                    <Button variant="contained" color="error" onClick={handleDeleteOrder} style={{ marginRight: "10px" }}>
                        Ta bort
                    </Button>
                    <Button variant="contained" color="secondary" onClick={handleCloseDialog}>Avbryt</Button>
                </DialogContent>
            </Dialog>

            <Dialog open={warningDialog} onClose={() => handleCloseWarning(false)}>
                <DialogTitle>Kollision upptäckt</DialogTitle>
                <DialogContent>
                    <p>Den valda tiden överlappar en befintlig bokning. Vill du ändå schemalägga?</p>
                    <Button variant="contained" color="primary" onClick={() => handleCloseWarning(true)}>Ja</Button>
                    <Button variant="contained" color="secondary" onClick={() => handleCloseWarning(false)}>Nej</Button>
                </DialogContent>
            </Dialog>
            {/* Dialog för att skapa en ny arbetsorder */}
            <Dialog open={newOrderDialog} onClose={() => setNewOrderDialog(false)}>
                <DialogTitle>Skapa ny arbetsorder</DialogTitle>
                <DialogContent>
                    <TextField
                        label="Titel"
                        fullWidth
                        margin="dense"
                        value={newOrderTitle}
                        onChange={(e) => setNewOrderTitle(e.target.value)}
                    />
                    <TextField
                        select
                        label="Mekaniker"
                        fullWidth
                        margin="dense"
                        value={newOrderMechanic}
                        onChange={(e) => setNewOrderMechanic(e.target.value)}
                    >
                        {mechanics.map((mec) => (
                            <MenuItem key={mec.id} value={mec.id}>
                                {mec.username} {/* 🔹 Visar användarnamnet på mekanikern */}
                            </MenuItem>
                        ))}
                    </TextField>
                    <p>
                        Starttid: <strong>{moment(newOrderStart).format("YYYY-MM-DD HH:mm")}</strong>
                    </p>
                    <p>
                        Sluttid: <strong>{moment(newOrderEnd).format("YYYY-MM-DD HH:mm")}</strong>
                    </p>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={handleCreateOrder}
                        style={{ marginRight: "10px" }}
                    >
                        Skapa
                    </Button>
                    <Button variant="contained" color="secondary" onClick={() => setNewOrderDialog(false)}>
                        Avbryt
                    </Button>
                </DialogContent>
            </Dialog>

        </div>
    );
};

export default CalendarView;