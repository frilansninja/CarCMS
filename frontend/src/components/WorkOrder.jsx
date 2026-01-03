import React, { useState } from "react";
import PartOrderList from "./PartOrderList";
import PartOrderForm from "./PartOrderForm";

const WorkOrderDetails = ({ workOrderId }) => {
    const [refreshKey, setRefreshKey] = useState(0);

    return (
        <div>
            <h2>Arbetsorder {workOrderId}</h2>

            <PartOrderForm workOrderId={workOrderId} onOrderCreated={() => setRefreshKey(prev => prev + 1)} />
            <PartOrderList key={refreshKey} workOrderId={workOrderId} />
        </div>
    );
};

export default WorkOrderDetails;
